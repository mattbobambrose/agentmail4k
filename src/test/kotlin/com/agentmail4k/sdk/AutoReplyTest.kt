package com.agentmail4k.sdk

import com.agentmail4k.dsl.autoReply
import com.agentmail4k.sdk.model.MessageList
import com.agentmail4k.sdk.model.SendMessageResponse
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class AutoReplyTest : StringSpec({

  "default reply is used when no rule matches" {
    val (client, mockMessages) = mockInboxClient()
    val msg = testMessage(messageId = "msg_default", subject = "Random topic")
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
        match = { it.subject == "Pricing" },
        reply = { text = "Pricing info" },
      )

      default {
        text = "Thank you for your message."
      }
    }

    testScope.advanceTimeBy(1_500)

    coVerify { mockMessages.reply("msg_default", any()) }

    job.cancel()
  }

  "no reply is sent when no rule matches and no default is set" {
    val (client, mockMessages) = mockInboxClient()
    val msg = testMessage(messageId = "msg_noreply", subject = "Unmatched")
    coEvery { mockMessages.list(any()) } returnsMany listOf(
      MessageList(count = 1, messages = listOf(msg)),
      MessageList(count = 0, messages = emptyList()),
    )

    val testScope = TestScope()

    val job = client.autoReply("inbox_1", scope = testScope) {
      pollInterval = 1.seconds

      rule(
        match = { it.subject == "Help" },
        reply = { text = "Help response" },
      )
    }

    testScope.advanceTimeBy(1_500)

    coVerify(exactly = 0) { mockMessages.reply(any(), any()) }

    job.cancel()
  }

  "first matching rule wins when multiple rules match" {
    val (client, mockMessages) = mockInboxClient()
    val msg = testMessage(messageId = "msg_multi", subject = "Help with pricing")
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
        match = { it.subject?.contains("Help") == true },
        reply = { text = "Help response" },
      )

      rule(
        match = { it.subject?.contains("pricing") == true },
        reply = { text = "Pricing response" },
      )
    }

    testScope.advanceTimeBy(1_500)

    coVerify(exactly = 1) { mockMessages.reply("msg_multi", any()) }

    job.cancel()
  }
})
