package com.agentmail4k.sdk

import com.agentmail4k.dsl.MonitorBuilder
import com.agentmail4k.dsl.monitor
import com.agentmail4k.dsl.poll
import com.agentmail4k.sdk.model.Attachment
import com.agentmail4k.sdk.model.Message
import com.agentmail4k.sdk.model.MessageList
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class MonitorTest : StringSpec({

  // --- MonitorBuilder validation ---

  "build throws when no message handler is set" {
    val builder = MonitorBuilder()
    val ex = shouldThrow<IllegalArgumentException> {
      builder.build()
    }
    ex.message shouldContain "onMessage or onFullMessage must be set"
  }

  // --- filterBy ---

  "filterBy skips messages that do not match the predicate" {
    val (client, mockMessages) = mockInboxClient()
    val matchMsg = testMessage(messageId = "msg_match", subject = "Important")
    val skipMsg = testMessage(messageId = "msg_skip", subject = "Spam")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 2, messages = listOf(matchMsg, skipMsg)),
      MessageList(count = 0, messages = emptyList()),
    )

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.monitor("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      filterBy { it.subject == "Important" }
      onMessage { received.add(it) }
    }

    testScope.advanceTimeBy(1_500)

    received.size shouldBe 1
    received[0].messageId shouldBe "msg_match"

    job.cancel()
  }

  "filterBy passes all messages when no filter is set" {
    val (client, mockMessages) = mockInboxClient()
    val msg1 = testMessage(messageId = "msg_1")
    val msg2 = testMessage(messageId = "msg_2")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 2, messages = listOf(msg1, msg2)),
      MessageList(count = 0, messages = emptyList()),
    )

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.monitor("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      onMessage { received.add(it) }
    }

    testScope.advanceTimeBy(1_500)

    received.size shouldBe 2

    job.cancel()
  }

  "filterBy runs on preview before toFullMessage fetch" {
    val (client, mockMessages) = mockInboxClient()
    val matchMsg = testMessage(messageId = "msg_match", subject = "Include")
    val skipMsg = testMessage(messageId = "msg_skip", subject = "Exclude")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 2, messages = listOf(matchMsg, skipMsg)),
      MessageList(count = 0, messages = emptyList()),
    )
    val fullMsg = testMessage(messageId = "msg_match", subject = "Include")
    coEvery { mockMessages.get("msg_match") } returns fullMsg

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.monitor("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      filterBy { it.subject == "Include" }
      onFullMessage { received.add(it) }
    }

    testScope.advanceTimeBy(1_500)

    received.size shouldBe 1
    coVerify(exactly = 1) { mockMessages.get("msg_match") }
    coVerify(exactly = 0) { mockMessages.get("msg_skip") }

    job.cancel()
  }

  "filterBy can filter on labels" {
    val (client, mockMessages) = mockInboxClient()
    val unread = testMessage(messageId = "msg_unread").copy(labels = listOf("unread"))
    val read = testMessage(messageId = "msg_read").copy(labels = listOf("read"))
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 2, messages = listOf(unread, read)),
      MessageList(count = 0, messages = emptyList()),
    )

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.monitor("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      filterBy { "unread" in it.labels }
      onMessage { received.add(it) }
    }

    testScope.advanceTimeBy(1_500)

    received.size shouldBe 1
    received[0].messageId shouldBe "msg_unread"

    job.cancel()
  }

  "filterBy can filter on attachments" {
    val (client, mockMessages) = mockInboxClient()
    val withAttachment = testMessage(messageId = "msg_att").copy(
      attachments = listOf(
        Attachment(
          attachmentId = "att_1",
          filename = "file.pdf",
          size = 1024,
          contentType = "application/pdf",
        )
      )
    )
    val withoutAttachment = testMessage(messageId = "msg_noatt")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 2, messages = listOf(withAttachment, withoutAttachment)),
      MessageList(count = 0, messages = emptyList()),
    )

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.monitor("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      filterBy { it.attachments.isNotEmpty() }
      onMessage { received.add(it) }
    }

    testScope.advanceTimeBy(1_500)

    received.size shouldBe 1
    received[0].messageId shouldBe "msg_att"

    job.cancel()
  }

  // --- onError ---

  "monitor invokes onError when polling throws" {
    val (client, mockMessages) = mockInboxClient()
    coEvery { mockMessages.list(any()) } throws RuntimeException("API failure")

    val errors = mutableListOf<Throwable>()
    val testScope = TestScope()

    val job = client.monitor("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      onMessage { }
      onError { errors.add(it) }
    }

    testScope.advanceTimeBy(500)

    errors.size shouldBe 1
    errors[0].message shouldBe "API failure"

    job.cancel()
  }

  "monitor continues polling after onError" {
    val (client, mockMessages) = mockInboxClient()
    val msg = testMessage(messageId = "msg_after_error")

    var callCount = 0
    coEvery { mockMessages.list(any()) } answers {
      callCount++
      when (callCount) {
        1 -> throw RuntimeException("transient")
        2 -> MessageList(count = 1, messages = listOf(msg))
        else -> MessageList(count = 0, messages = emptyList())
      }
    }

    val received = mutableListOf<Message>()
    val errors = mutableListOf<Throwable>()
    val testScope = TestScope()

    val job = client.monitor("inbox_1", scope = testScope) {
      pollInterval = 1.seconds
      onMessage { received.add(it) }
      onError { errors.add(it) }
    }

    testScope.advanceTimeBy(1_500)

    errors.size shouldBe 1
    received.size shouldBe 1
    received[0].messageId shouldBe "msg_after_error"

    job.cancel()
  }

  // --- poll ---

  "poll delegates to monitor with correct interval" {
    val (client, mockMessages) = mockInboxClient()
    val msg = testMessage(messageId = "msg_poll")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 1, messages = listOf(msg)),
      MessageList(count = 0, messages = emptyList()),
    )

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.poll("inbox_1", interval = 2.seconds, scope = testScope) { message ->
      received.add(message)
    }

    testScope.advanceTimeBy(2_500)

    received.size shouldBe 1
    received[0].messageId shouldBe "msg_poll"

    job.cancel()
  }

  "poll filter parameter applies filterBy" {
    val (client, mockMessages) = mockInboxClient()
    val match = testMessage(messageId = "msg_yes", from = "vip@example.com")
    val skip = testMessage(messageId = "msg_no", from = "spam@example.com")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 2, messages = listOf(match, skip)),
      MessageList(count = 0, messages = emptyList()),
    )

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.poll(
      "inbox_1",
      interval = 1.seconds,
      filter = { it.from.endsWith("@example.com") && it.from.startsWith("vip") },
      scope = testScope,
    ) { received.add(it) }

    testScope.advanceTimeBy(1_500)

    received.size shouldBe 1
    received[0].messageId shouldBe "msg_yes"

    job.cancel()
  }

  "poll without filter processes all messages" {
    val (client, mockMessages) = mockInboxClient()
    val msg1 = testMessage(messageId = "msg_a")
    val msg2 = testMessage(messageId = "msg_b")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 2, messages = listOf(msg1, msg2)),
      MessageList(count = 0, messages = emptyList()),
    )

    val received = mutableListOf<Message>()
    val testScope = TestScope()

    val job = client.poll("inbox_1", interval = 1.seconds, scope = testScope) { message ->
      received.add(message)
    }

    testScope.advanceTimeBy(1_500)

    received.size shouldBe 2

    job.cancel()
  }
})
