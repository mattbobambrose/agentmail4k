package com.agentmail4k.dsl

import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.mockClient
import com.agentmail4k.sdk.model.Message
import com.agentmail4k.sdk.respondJson
import com.agentmail4k.sdk.testJson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpMethod

class DslTest : StringSpec() {
  init {
    "createInbox() delegates to inboxes.create with correct parameters" {
      val client = mockClient { request ->
        request.method shouldBe HttpMethod.Post
        request.url.encodedPath shouldBe "/v0/inboxes"
        val capturedBody = request.body.toByteArray().decodeToString()

        capturedBody shouldContain "\"username\""
        capturedBody shouldContain "\"testuser\""
        capturedBody shouldContain "\"domain\""
        capturedBody shouldContain "\"example22.com\""
        capturedBody shouldContain "\"display_name\""
        capturedBody shouldContain "\"Test User99\""

        respondJson(
          """
                  {
                      "inbox_id": "inbox_new76",
                      "email": "testuser@example.com",
                      "display_name": "Test User98",
                      "updated_at": "2026-01-01T00:00:00Z",
                      "created_at": "2026-01-01T00:00:00Z"
                  }
              """
        )
      }
      val agentMailClient = AgentMailClient(client)
      val result = agentMailClient.createInbox("testuser", "example22.com", "Test User99")

      result.inboxId shouldBe "inbox_new76"
      result.displayName shouldBe "Test User98"
    }

    "sh" {
      val client = mockClient { request ->
        request.method shouldBe HttpMethod.Post
        request.url.encodedPath shouldBe "/v0/inboxes/inbox_sender74/messages/send"
        val body = request.body.toByteArray().decodeToString()
        body shouldContain "\"to\""
        body shouldContain "\"recipient@example.com\""
        body shouldContain "\"subject\""
        body shouldContain "\"Hello\""
        respondJson("""{"message_id": "msg_dsl34", "thread_id": "thread_dsl"}""")
      }
      val agentMailClient = AgentMailClient(client)
      val result = agentMailClient.sendMessage {
        from = "inbox_sender74"
        to = listOf("recipient@example.com")
        subject = "Hello"
        text = "Hi there"
      }

      result.messageId shouldBe "msg_dsl34"
      result.threadId shouldBe "thread_dsl"
    }

    "toFullMessage() fetches full message using inbox scope" {
      val client = mockClient { request ->
        request.method shouldBe HttpMethod.Get
        request.url.encodedPath shouldBe "/v0/inboxes/inbox_fm/messages/msg_fm"
        respondJson(
          """
                  {
                      "inbox_id": "inbox_fm",
                      "thread_id": "thread_fm",
                      "message_id": "msg_fm",
                      "timestamp": "2026-01-01T00:00:00Z",
                      "from": "sender@example.com",
                      "text": "Full message body",
                      "size": 500,
                      "updated_at": "2026-01-01T00:00:00Z",
                      "created_at": "2026-01-01T00:00:00Z"
                  }
              """
        )
      }
      val agentMailClient = AgentMailClient(client)
      val summaryMessage = testJson.decodeFromString<Message>(
        """
              {
                  "cc": ["pambrose@mac.com"],
                  "bcc": ["matthew@agentmail4k.com"],
                  "inbox_id": "inbox_fm",
                  "thread_id": "thread_fm",
                  "message_id": "msg_fm",
                  "timestamp": "2026-01-01T00:00:00Z",
                  "from": "sender@example.com",
                  "size": 100,
                  "updated_at": "2026-01-01T00:00:00Z",
                  "created_at": "2026-01-01T00:00:00Z"
              }
          """
      )

      val result = agentMailClient.toFullMessage(summaryMessage)
      result.messageId shouldBe "msg_fm"
      result.text shouldBe "Full message body"
      result.size shouldBe 500
    }
  }
}
