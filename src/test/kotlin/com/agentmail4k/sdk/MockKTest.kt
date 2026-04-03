package com.agentmail4k.sdk

import com.agentmail4k.sdk.model.Inbox
import com.agentmail4k.sdk.model.InboxList
import com.agentmail4k.sdk.model.Message
import com.agentmail4k.sdk.model.MessageList
import com.agentmail4k.sdk.model.SendMessageResponse
import com.agentmail4k.sdk.resource.InboxResource
import com.agentmail4k.sdk.resource.PodResource
import com.agentmail4k.sdk.resource.PodScope
import com.agentmail4k.dsl.autoReply
import com.agentmail4k.dsl.bulk
import com.agentmail4k.dsl.monitor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class MockKTest : StringSpec({

  "mocked inboxes list returns correct InboxList" {
    val expected = InboxList(
      count = 2,
      inboxes = listOf(
        Inbox(inboxId = "inbox_1", email = "a@agentmail.to", updatedAt = testInstant, createdAt = testInstant),
        Inbox(inboxId = "inbox_2", email = "b@agentmail.to", updatedAt = testInstant, createdAt = testInstant),
      ),
    )

    val mockInboxResource = mockk<InboxResource>()
    coEvery { mockInboxResource.list(any()) } returns expected

    val client = mockk<AgentMailClient>(relaxUnitFun = true) {
      every { inboxes } returns mockInboxResource
    }

    val result = client.inboxes.list()
    result.count shouldBe 2
    result.inboxes[0].inboxId shouldBe "inbox_1"
    result.inboxes[1].email shouldBe "b@agentmail.to"
  }

  "mocked pods property returns PodResource with correct count" {
    val mockPodResource = mockk<PodResource>()
    val client = mockk<AgentMailClient>(relaxUnitFun = true) {
      every { pods } returns mockPodResource
    }
    coEvery { mockPodResource.list(any()) } returns mockk {
      every { count } returns 3
    }

    client.pods.list().count shouldBe 3
  }

  "mocked inbox scope messages list returns correct MessageList" {
    val (client, mockMessages) = mockInboxClient()
    val expected = MessageList(count = 1, messages = listOf(testMessage()))
    coEvery { mockMessages.list(any()) } returns expected

    val result = client.inboxes("inbox_1").messages.list()
    result.count shouldBe 1
    result.messages[0].messageId shouldBe "msg_1"
  }

  "mocked pod scope inboxes returns correct InboxResource" {
    val mockInboxResource = mockk<InboxResource>()
    val mockPodScope = mockk<PodScope> {
      every { inboxes } returns mockInboxResource
    }
    val client = mockk<AgentMailClient>(relaxUnitFun = true) {
      every { pods("pod_1") } returns mockPodScope
    }
    coEvery { mockInboxResource.list(any()) } returns InboxList(count = 0, inboxes = emptyList())

    client.pods("pod_1").inboxes.list().count shouldBe 0
  }

  "bulk send calls messages send for each recipient" {
    val (client, mockMessages) = mockInboxClient()
    coEvery { mockMessages.send(any()) } returns SendMessageResponse(
      messageId = "msg_new",
      threadId = "thread_new",
    )

    val results = client.bulk {
      send("inbox_1", listOf("alice@example.com", "bob@example.com")) {
        subject = "Hello"
        text = "Hi there"
      }
    }

    results.size shouldBe 2
    coVerify(exactly = 2) { mockMessages.send(any()) }
  }

  "monitor invokes onMessage for each new message" {
    val (client, mockMessages) = mockInboxClient()
    val msg = testMessage(messageId = "msg_monitor")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 1, messages = listOf(msg)),
      MessageList(count = 0, messages = emptyList()),
    )

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.monitor("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      onMessage { received.add(it) }
    }

    testScope.advanceTimeBy(1_500)

    received.size shouldBe 1
    received[0].messageId shouldBe "msg_monitor"

    job.cancel()
  }

  "autoReply sends reply when a rule matches" {
    val (client, mockMessages) = mockInboxClient()
    val msg = testMessage(messageId = "msg_auto", subject = "Help")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 1, messages = listOf(msg)),
      MessageList(count = 0, messages = emptyList()),
    )
    coEvery { mockMessages.reply(any(), any()) } returns SendMessageResponse(
      messageId = "msg_reply",
      threadId = "thread_1",
    )

    val testScope = TestScope()

    val job = client.autoReply("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      rule(
        match = { it.subject == "Help" },
        reply = { text = "We can help!" },
      )
    }

    testScope.advanceTimeBy(1_500)

    coVerify { mockMessages.reply("msg_auto", any()) }

    job.cancel()
  }
})
