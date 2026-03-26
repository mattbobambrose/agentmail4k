package com.mattbobambrose.agentmail4k.sdk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpMethod
import com.mattbobambrose.agentmail4k.dsl.createInbox
import com.mattbobambrose.agentmail4k.dsl.fullMessage
import com.mattbobambrose.agentmail4k.dsl.sendMessage
import com.mattbobambrose.agentmail4k.sdk.model.Message

class DslTest : StringSpec() {
  init {
    "createInbox() delegates to inboxes.create with correct parameters" {
      var capturedBody = ""
      val client = mockClient { request ->
        request.method shouldBe HttpMethod.Post
        request.url.encodedPath shouldBe "/v0/inboxes"
        capturedBody = request.body.toByteArray().decodeToString()
        respondJson(
          """
                  {
                      "inbox_id": "inbox_new",
                      "email": "testuser@example.com",
                      "display_name": "Test User",
                      "updated_at": "2026-01-01T00:00:00Z",
                      "created_at": "2026-01-01T00:00:00Z"
                  }
              """
        )
      }
      val agentMailClient = AgentMailClient(client)
      val result = agentMailClient.createInbox("testuser", "example.com", "Test User")

      result.inboxId shouldBe "inbox_new"
      result.displayName shouldBe "Test User"
      capturedBody shouldContain "\"username\""
      capturedBody shouldContain "\"testuser\""
      capturedBody shouldContain "\"domain\""
      capturedBody shouldContain "\"example.com\""
      capturedBody shouldContain "\"display_name\""
      capturedBody shouldContain "\"Test User\""
    }

    "sendMessage() routes to correct inbox based on from field" {
      val client = mockClient { request ->
        request.method shouldBe HttpMethod.Post
        request.url.encodedPath shouldBe "/v0/inboxes/inbox_sender/messages/send"
        val body = request.body.toByteArray().decodeToString()
        body shouldContain "\"to\""
        body shouldContain "\"recipient@example.com\""
        body shouldContain "\"subject\""
        body shouldContain "\"Hello\""
        respondJson("""{"message_id": "msg_dsl", "thread_id": "thread_dsl"}""")
      }
      val agentMailClient = AgentMailClient(client)
      val result = agentMailClient.sendMessage {
        from = "inbox_sender"
        to = listOf("recipient@example.com")
        subject = "Hello"
        text = "Hi there"
      }

      result.messageId shouldBe "msg_dsl"
      result.threadId shouldBe "thread_dsl"
    }

    "fullMessage() fetches full message using inbox scope" {
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

      val result = agentMailClient.fullMessage(summaryMessage)
      result.messageId shouldBe "msg_fm"
      result.text shouldBe "Full message body"
      result.size shouldBe 500
    }
  }
}
