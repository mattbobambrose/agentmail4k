package com.agentmail4k.sdk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import com.agentmail4k.sdk.model.ApiKey
import com.agentmail4k.sdk.model.Attachment
import com.agentmail4k.sdk.model.ContentDisposition
import com.agentmail4k.sdk.model.CreateApiKeyResponse
import com.agentmail4k.sdk.model.Domain
import com.agentmail4k.sdk.model.Draft
import com.agentmail4k.sdk.model.Inbox
import com.agentmail4k.sdk.model.InboxList
import com.agentmail4k.sdk.model.ListEntry
import com.agentmail4k.sdk.model.Message
import com.agentmail4k.sdk.model.Metric
import com.agentmail4k.sdk.model.Pod
import com.agentmail4k.sdk.model.Thread
import com.agentmail4k.sdk.model.Webhook

class ModelTest : StringSpec({

  val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
    explicitNulls = false
  }

  "Inbox deserialization maps snake_case fields to camelCase properties" {
    val raw = """
        {
            "inbox_id": "inbox-1",
            "pod_id": "pod-1",
            "email": "test@example.com",
            "display_name": "Test Inbox",
            "client_id": "client-1",
            "created_at": "2024-01-01T00:00:00Z",
            "updated_at": "2024-06-15T12:30:00Z"
        }
        """.trimIndent()

    val inbox = json.decodeFromString<Inbox>(raw)

    inbox.inboxId shouldBe "inbox-1"
    inbox.podId shouldBe "pod-1"
    inbox.email shouldBe "test@example.com"
    inbox.displayName shouldBe "Test Inbox"
    inbox.clientId shouldBe "client-1"
    inbox.createdAt shouldBe Instant.parse("2024-01-01T00:00:00Z")
    inbox.updatedAt shouldBe Instant.parse("2024-06-15T12:30:00Z")
  }

  "InboxList deserialization parses next_page_token and inbox array" {
    val raw = """
        {
            "count": 2,
            "limit": 10,
            "next_page_token": "token-abc",
            "inboxes": [
                {
                    "inbox_id": "inbox-1",
                    "email": "a@example.com",
                    "created_at": "2024-01-01T00:00:00Z",
                    "updated_at": "2024-01-01T00:00:00Z"
                },
                {
                    "inbox_id": "inbox-2",
                    "email": "b@example.com",
                    "created_at": "2024-02-01T00:00:00Z",
                    "updated_at": "2024-02-01T00:00:00Z"
                }
            ]
        }
        """.trimIndent()

    val inboxList = json.decodeFromString<InboxList>(raw)

    inboxList.count shouldBe 2
    inboxList.limit shouldBe 10
    inboxList.nextPageToken shouldBe "token-abc"
    inboxList.inboxes shouldHaveSize 2
    inboxList.inboxes[0].inboxId shouldBe "inbox-1"
    inboxList.inboxes[1].inboxId shouldBe "inbox-2"
  }

  "Message deserialization with all fields including in_reply_to, attachments, and headers" {
    val raw = """
        {
            "inbox_id": "inbox-1",
            "thread_id": "thread-1",
            "message_id": "msg-1",
            "labels": ["inbox", "unread"],
            "timestamp": "2024-03-10T08:00:00Z",
            "from": "sender@example.com",
            "to": ["recipient@example.com"],
            "cc": ["cc@example.com"],
            "bcc": ["bcc@example.com"],
            "subject": "Hello",
            "preview": "Hello world...",
            "text": "Hello world",
            "html": "<p>Hello world</p>",
            "attachments": [
                {
                    "attachment_id": "att-1",
                    "filename": "file.pdf",
                    "size": 1024,
                    "content_type": "application/pdf",
                    "content_disposition": "attachment"
                }
            ],
            "in_reply_to": "msg-0",
            "references": ["msg-0"],
            "headers": {"X-Custom": "value"},
            "size": 2048,
            "updated_at": "2024-03-10T08:00:00Z",
            "created_at": "2024-03-10T08:00:00Z"
        }
        """.trimIndent()

    val message = json.decodeFromString<Message>(raw)

    message.inboxId shouldBe "inbox-1"
    message.threadId shouldBe "thread-1"
    message.messageId shouldBe "msg-1"
    message.labels shouldContainExactly listOf("inbox", "unread")
    message.from shouldBe "sender@example.com"
    message.to shouldContainExactly listOf("recipient@example.com")
    message.cc shouldContainExactly listOf("cc@example.com")
    message.bcc shouldContainExactly listOf("bcc@example.com")
    message.subject shouldBe "Hello"
    message.preview shouldBe "Hello world..."
    message.text shouldBe "Hello world"
    message.html shouldBe "<p>Hello world</p>"
    message.attachments shouldHaveSize 1
    message.attachments[0].attachmentId shouldBe "att-1"
    message.attachments[0].contentDisposition shouldBe ContentDisposition.ATTACHMENT
    message.inReplyTo shouldBe "msg-0"
    message.references shouldContainExactly listOf("msg-0")
    message.headers shouldBe mapOf("X-Custom" to "value")
    message.size shouldBe 2048
  }

  "Thread deserialization with last_message_id, message_count, and timestamps" {
    val raw = """
        {
            "inbox_id": "inbox-1",
            "thread_id": "thread-1",
            "labels": ["inbox"],
            "timestamp": "2024-04-01T10:00:00Z",
            "received_timestamp": "2024-04-01T09:00:00Z",
            "sent_timestamp": "2024-04-01T10:00:00Z",
            "senders": ["alice@example.com"],
            "recipients": ["bob@example.com"],
            "subject": "Meeting",
            "preview": "Let's meet...",
            "attachments": [],
            "last_message_id": "msg-5",
            "message_count": 3,
            "size": 4096,
            "updated_at": "2024-04-01T10:00:00Z",
            "created_at": "2024-04-01T09:00:00Z"
        }
        """.trimIndent()

    val thread = json.decodeFromString<Thread>(raw)

    thread.inboxId shouldBe "inbox-1"
    thread.threadId shouldBe "thread-1"
    thread.lastMessageId shouldBe "msg-5"
    thread.messageCount shouldBe 3
    thread.receivedTimestamp shouldBe Instant.parse("2024-04-01T09:00:00Z")
    thread.sentTimestamp shouldBe Instant.parse("2024-04-01T10:00:00Z")
    thread.senders shouldContainExactly listOf("alice@example.com")
    thread.recipients shouldContainExactly listOf("bob@example.com")
    thread.subject shouldBe "Meeting"
    thread.size shouldBe 4096
  }

  "Draft deserialization with optional fields null" {
    val raw = """
        {
            "inbox_id": "inbox-1",
            "draft_id": "draft-1",
            "timestamp": "2024-05-01T00:00:00Z",
            "updated_at": "2024-05-01T00:00:00Z",
            "created_at": "2024-05-01T00:00:00Z"
        }
        """.trimIndent()

    val draft = json.decodeFromString<Draft>(raw)

    draft.inboxId shouldBe "inbox-1"
    draft.draftId shouldBe "draft-1"
    draft.from.shouldBeNull()
    draft.subject.shouldBeNull()
    draft.text.shouldBeNull()
    draft.html.shouldBeNull()
    draft.preview.shouldBeNull()
    draft.to.shouldBeEmpty()
    draft.cc.shouldBeEmpty()
    draft.bcc.shouldBeEmpty()
    draft.labels.shouldBeEmpty()
    draft.attachments.shouldBeEmpty()
  }

  "Domain deserialization with domain_id and verified boolean" {
    val raw = """
        {
            "domain_id": "domain-1",
            "name": "example.com",
            "verified": true,
            "updated_at": "2024-01-01T00:00:00Z",
            "created_at": "2024-01-01T00:00:00Z"
        }
        """.trimIndent()

    val domain = json.decodeFromString<Domain>(raw)

    domain.domainId shouldBe "domain-1"
    domain.name shouldBe "example.com"
    domain.verified shouldBe true
  }

  "Pod deserialization with pod_id" {
    val raw = """
        {
            "pod_id": "pod-1",
            "updated_at": "2024-01-01T00:00:00Z",
            "created_at": "2024-01-01T00:00:00Z"
        }
        """.trimIndent()

    val pod = json.decodeFromString<Pod>(raw)

    pod.podId shouldBe "pod-1"
    pod.updatedAt shouldBe Instant.parse("2024-01-01T00:00:00Z")
    pod.createdAt shouldBe Instant.parse("2024-01-01T00:00:00Z")
  }

  "Webhook deserialization with events list" {
    val raw = """
        {
            "webhook_id": "wh-1",
            "url": "https://example.com/webhook",
            "events": ["message.received", "message.sent"],
            "updated_at": "2024-01-01T00:00:00Z",
            "created_at": "2024-01-01T00:00:00Z"
        }
        """.trimIndent()

    val webhook = json.decodeFromString<Webhook>(raw)

    webhook.webhookId shouldBe "wh-1"
    webhook.url shouldBe "https://example.com/webhook"
    webhook.events shouldContainExactly listOf("message.received", "message.sent")
  }

  "Attachment deserialization with content_disposition inline enum" {
    val raw = """
        {
            "attachment_id": "att-1",
            "filename": "image.png",
            "size": 512,
            "content_type": "image/png",
            "content_disposition": "inline",
            "content_id": "cid-1"
        }
        """.trimIndent()

    val attachment = json.decodeFromString<Attachment>(raw)

    attachment.attachmentId shouldBe "att-1"
    attachment.filename shouldBe "image.png"
    attachment.size shouldBe 512
    attachment.contentType shouldBe "image/png"
    attachment.contentDisposition shouldBe ContentDisposition.INLINE
    attachment.contentId shouldBe "cid-1"
  }

  "Attachment deserialization with content_disposition attachment enum" {
    val raw = """
        {
            "attachment_id": "att-2",
            "size": 256,
            "content_disposition": "attachment"
        }
        """.trimIndent()

    val attachment = json.decodeFromString<Attachment>(raw)

    attachment.contentDisposition shouldBe ContentDisposition.ATTACHMENT
  }

  "ContentDisposition enum serializes to inline and attachment strings" {
    val inlineJson = json.encodeToString(ContentDisposition.INLINE)
    val attachmentJson = json.encodeToString(ContentDisposition.ATTACHMENT)

    inlineJson shouldBe "\"inline\""
    attachmentJson shouldBe "\"attachment\""
  }

  "ListEntry deserialization verifies snake_case mapping" {
    val raw = """
        {
            "entry": "sender@example.com",
            "updated_at": "2024-06-01T00:00:00Z",
            "created_at": "2024-06-01T00:00:00Z"
        }
        """.trimIndent()

    val listEntry = json.decodeFromString<ListEntry>(raw)

    listEntry.entry shouldBe "sender@example.com"
    listEntry.updatedAt shouldBe Instant.parse("2024-06-01T00:00:00Z")
    listEntry.createdAt shouldBe Instant.parse("2024-06-01T00:00:00Z")
  }

  "Metric deserialization verifies event_type mapping" {
    val raw = """
        {
            "event_type": "message.received",
            "count": 42,
            "timestamp": "2024-07-01T00:00:00Z"
        }
        """.trimIndent()

    val metric = json.decodeFromString<Metric>(raw)

    metric.eventType shouldBe "message.received"
    metric.count shouldBe 42
    metric.timestamp shouldBe Instant.parse("2024-07-01T00:00:00Z")
  }

  "ApiKey and CreateApiKeyResponse verify api_key_id and api_key fields" {
    val apiKeyRaw = """
        {
            "api_key_id": "key-1",
            "name": "My Key",
            "updated_at": "2024-01-01T00:00:00Z",
            "created_at": "2024-01-01T00:00:00Z"
        }
        """.trimIndent()

    val apiKey = json.decodeFromString<ApiKey>(apiKeyRaw)
    apiKey.apiKeyId shouldBe "key-1"
    apiKey.name shouldBe "My Key"

    val createRaw = """
        {
            "api_key_id": "key-2",
            "api_key": "sk-secret-value"
        }
        """.trimIndent()

    val createResp = json.decodeFromString<CreateApiKeyResponse>(createRaw)
    createResp.apiKeyId shouldBe "key-2"
    createResp.apiKey shouldBe "sk-secret-value"
  }

  "Unknown fields are ignored during deserialization" {
    val raw = """
        {
            "pod_id": "pod-1",
            "updated_at": "2024-01-01T00:00:00Z",
            "created_at": "2024-01-01T00:00:00Z",
            "some_unknown_field": "should be ignored",
            "another_extra": 999
        }
        """.trimIndent()

    val pod = json.decodeFromString<Pod>(raw)

    pod.podId shouldBe "pod-1"
  }

  "Optional fields default correctly when parsing minimal JSON" {
    val inboxRaw = """
        {
            "inbox_id": "inbox-1",
            "email": "test@example.com",
            "updated_at": "2024-01-01T00:00:00Z",
            "created_at": "2024-01-01T00:00:00Z"
        }
        """.trimIndent()

    val inbox = json.decodeFromString<Inbox>(inboxRaw)
    inbox.podId.shouldBeNull()
    inbox.displayName.shouldBeNull()
    inbox.clientId.shouldBeNull()

    val messageRaw = """
        {
            "inbox_id": "inbox-1",
            "thread_id": "thread-1",
            "message_id": "msg-1",
            "timestamp": "2024-01-01T00:00:00Z",
            "from": "sender@example.com",
            "size": 100,
            "updated_at": "2024-01-01T00:00:00Z",
            "created_at": "2024-01-01T00:00:00Z"
        }
        """.trimIndent()

    val message = json.decodeFromString<Message>(messageRaw)
    message.labels.shouldBeEmpty()
    message.to.shouldBeEmpty()
    message.cc.shouldBeEmpty()
    message.bcc.shouldBeEmpty()
    message.subject.shouldBeNull()
    message.preview.shouldBeNull()
    message.text.shouldBeNull()
    message.html.shouldBeNull()
    message.attachments.shouldBeEmpty()
    message.inReplyTo.shouldBeNull()
    message.references.shouldBeEmpty()
    message.headers shouldBe emptyMap()
  }
})
