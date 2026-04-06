package com.agentmail4k.sdk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import com.agentmail4k.sdk.internal.ApiPaths
import com.agentmail4k.sdk.model.ListDirection
import com.agentmail4k.sdk.model.ListType
import com.agentmail4k.sdk.resource.ApiKeyResource
import com.agentmail4k.sdk.resource.DomainResource
import com.agentmail4k.sdk.resource.DraftResource
import com.agentmail4k.sdk.resource.InboxResource
import com.agentmail4k.sdk.resource.InboxScope
import com.agentmail4k.sdk.resource.ListResource
import com.agentmail4k.sdk.resource.MessageResource
import com.agentmail4k.sdk.resource.MetricsResource
import com.agentmail4k.sdk.resource.OrganizationResource
import com.agentmail4k.sdk.resource.PodResource
import com.agentmail4k.sdk.resource.PodScope
import com.agentmail4k.sdk.resource.ThreadResource
import com.agentmail4k.sdk.resource.WebhookResource

class ResourceTest : StringSpec({

  // --- InboxResource ---

  "InboxResource.list() sends GET to v0/inboxes and deserializes response" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes"
      respondJson(
        """
                {
                    "count": 1,
                    "inboxes": [{
                        "inbox_id": "inbox_1",
                        "email": "test@agentmail.to",
                        "updated_at": "2026-01-01T00:00:00Z",
                        "created_at": "2026-01-01T00:00:00Z"
                    }]
                }
            """
      )
    }
    val resource = InboxResource(client, ApiPaths.INBOXES)
    val result = resource.list()
    result.count shouldBe 1
    result.inboxes[0].inboxId shouldBe "inbox_1"
    result.inboxes[0].email shouldBe "test@agentmail.to"
  }

  "InboxResource.create() sends POST to v0/inboxes with JSON body" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/inboxes"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"username\""
      body shouldContain "\"testuser\""
      respondJson(
        """
                {
                    "inbox_id": "inbox_2",
                    "email": "testuser@agentmail.to",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = InboxResource(client, ApiPaths.INBOXES)
    val result = resource.create {
      username = "testuser"
    }
    result.inboxId shouldBe "inbox_2"
    result.email shouldBe "testuser@agentmail.to"
  }

  "InboxResource.get() sends GET to v0/inboxes/{inboxId}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_123"
      respondJson(
        """
                {
                    "inbox_id": "inbox_123",
                    "email": "user@agentmail.to",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = InboxResource(client, ApiPaths.INBOXES)
    val result = resource.get("inbox_123")
    result.inboxId shouldBe "inbox_123"
  }

  "InboxResource.delete() sends DELETE to v0/inboxes/{inboxId}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Delete
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_456"
      respondJson("{}")
    }
    val resource = InboxResource(client, ApiPaths.INBOXES)
    resource.delete("inbox_456")
  }

  // --- MessageResource ---

  "MessageResource.send() sends POST to basePath/send with body" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/send"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"to\""
      body shouldContain "\"recipient@example.com\""
      body shouldContain "\"subject\""
      body shouldContain "\"Hello\""
      respondJson(
        """
                {
                    "message_id": "msg_1",
                    "thread_id": "thread_1"
                }
            """
      )
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.send {
      to = listOf("recipient@example.com")
      subject = "Hello"
      text = "Hi there"
    }
    result.messageId shouldBe "msg_1"
    result.threadId shouldBe "thread_1"
  }

  "MessageResource.reply() sends POST to basePath/{messageId}/reply" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/msg_1/reply"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"text\""
      body shouldContain "\"Reply text\""
      respondJson(
        """
                {
                    "message_id": "msg_2",
                    "thread_id": "thread_1"
                }
            """
      )
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.reply("msg_1") {
      text = "Reply text"
    }
    result.messageId shouldBe "msg_2"
    result.threadId shouldBe "thread_1"
  }

  // --- ThreadResource ---

  "ThreadResource.list() with query params appends them to URL" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/threads"
      request.url.parameters["limit"] shouldBe "10"
      request.url.parameters["labels"] shouldBe "inbox,important"
      request.url.parameters["ascending"] shouldBe "true"
      respondJson(
        """
                {
                    "count": 0,
                    "threads": []
                }
            """
      )
    }
    val resource = ThreadResource(client, ApiPaths.THREADS)
    val result = resource.list {
      limit = 10
      labels = listOf("inbox", "important")
      ascending = true
    }
    result.count shouldBe 0
    result.threads shouldBe emptyList()
  }

  // --- DomainResource ---

  "DomainResource.verify() sends POST to v0/domains/{domainId}/verify" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/domains/domain_1/verify"
      respondJson("{}")
    }
    val resource = DomainResource(client, ApiPaths.DOMAINS)
    resource.verify("domain_1")
  }

  // --- PodResource ---

  "PodResource.create() sends POST to v0/pods" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/pods"
      respondJson(
        """
                {
                    "pod_id": "pod_1",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = PodResource(client, ApiPaths.PODS)
    val result = resource.create()
    result.podId shouldBe "pod_1"
  }

  // --- WebhookResource ---

  "WebhookResource.create() sends POST to v0/webhooks with body" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/webhooks"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"url\""
      body shouldContain "\"https://example.com/hook\""
      body shouldContain "\"events\""
      respondJson(
        """
                {
                    "webhook_id": "wh_1",
                    "url": "https://example.com/hook",
                    "events": ["message.received"],
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = WebhookResource(client, ApiPaths.WEBHOOKS)
    val result = resource.create {
      url = "https://example.com/hook"
      events = listOf("message.received")
    }
    result.webhookId shouldBe "wh_1"
    result.url shouldBe "https://example.com/hook"
    result.events shouldBe listOf("message.received")
  }

  // --- OrganizationResource ---

  "OrganizationResource.get() sends GET to v0/organizations" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/organizations"
      respondJson(
        """
                {
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = OrganizationResource(client, ApiPaths.ORGANIZATIONS)
    val result = resource.get()
    result.updatedAt.toString() shouldBe "2026-01-01T00:00:00Z"
  }

  // --- ApiKeyResource ---

  "ApiKeyResource.create() sends POST to v0/api-keys with body" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/api-keys"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"name\""
      body shouldContain "\"my-key\""
      respondJson(
        """
                {
                    "api_key_id": "key_1",
                    "api_key": "sk_test_abc123"
                }
            """
      )
    }
    val resource = ApiKeyResource(client, ApiPaths.API_KEYS)
    val result = resource.create {
      name = "my-key"
    }
    result.apiKeyId shouldBe "key_1"
    result.apiKey shouldBe "sk_test_abc123"
  }

  // --- InboxScope chains ---

  "InboxScope messages use correct base path v0/inboxes/{inboxId}/messages" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_99/messages"
      respondJson(
        """
                {
                    "count": 0,
                    "messages": []
                }
            """
      )
    }
    val scope = InboxScope(client, "inbox_99")
    val result = scope.messages.list()
    result.count shouldBe 0
  }

  "InboxScope threads use correct base path v0/inboxes/{inboxId}/threads" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_99/threads"
      respondJson(
        """
                {
                    "count": 0,
                    "threads": []
                }
            """
      )
    }
    val scope = InboxScope(client, "inbox_99")
    val result = scope.threads.list()
    result.count shouldBe 0
  }

  "InboxScope drafts use correct base path v0/inboxes/{inboxId}/drafts" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_99/drafts"
      respondJson(
        """
                {
                    "count": 0,
                    "drafts": []
                }
            """
      )
    }
    val scope = InboxScope(client, "inbox_99")
    val result = scope.drafts.list()
    result.count shouldBe 0
  }

  // --- PodScope chains ---

  "PodScope inboxes use correct base path v0/pods/{podId}/inboxes" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/pods/pod_42/inboxes"
      respondJson(
        """
                {
                    "count": 0,
                    "inboxes": []
                }
            """
      )
    }
    val scope = PodScope(client, "pod_42")
    val result = scope.inboxes.list()
    result.count shouldBe 0
  }

  "PodScope threads use correct base path v0/pods/{podId}/threads" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/pods/pod_42/threads"
      respondJson(
        """
                {
                    "count": 0,
                    "threads": []
                }
            """
      )
    }
    val scope = PodScope(client, "pod_42")
    val result = scope.threads.list()
    result.count shouldBe 0
  }

  "PodScope domains use correct base path v0/pods/{podId}/domains" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/pods/pod_42/domains"
      respondJson(
        """
                {
                    "count": 0,
                    "domains": []
                }
            """
      )
    }
    val scope = PodScope(client, "pod_42")
    val result = scope.domains.list()
    result.count shouldBe 0
  }

  // --- URL encoding of special characters ---

  "InboxResource.get() should URL-encode special characters in inboxId" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox%20with%20spaces"
      respondJson(
        """
                {
                    "inbox_id": "inbox with spaces",
                    "email": "test@agentmail.to",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = InboxResource(client, ApiPaths.INBOXES)
    val result = resource.get("inbox with spaces")
    result.inboxId shouldBe "inbox with spaces"
  }

  "MessageResource.get() should URL-encode special characters in messageId" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/msg%2Fspecial"
      respondJson(
        """
                {
                    "inbox_id": "inbox_1",
                    "thread_id": "thread_1",
                    "message_id": "msg/special",
                    "timestamp": "2026-01-01T00:00:00Z",
                    "from": "a@b.com",
                    "size": 100,
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.get("msg/special")
    result.messageId shouldBe "msg/special"
  }

  // --- Untested resource operations ---

  "InboxResource.update() sends PATCH with JSON body" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Patch
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"display_name\""
      body shouldContain "\"Updated Name\""
      respondJson(
        """
                {
                    "inbox_id": "inbox_1",
                    "email": "test@agentmail.to",
                    "display_name": "Updated Name",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = InboxResource(client, ApiPaths.INBOXES)
    val result = resource.update("inbox_1") { displayName = "Updated Name" }
    result.displayName shouldBe "Updated Name"
  }

  "MessageResource.replyAll() sends POST to basePath/{id}/reply-all" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/msg_1/reply-all"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"text\""
      body shouldContain "\"Reply all text\""
      respondJson("""{"message_id": "msg_3", "thread_id": "thread_1"}""")
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.replyAll("msg_1") { text = "Reply all text" }
    result.messageId shouldBe "msg_3"
  }

  "MessageResource.forward() sends POST to basePath/{id}/forward" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/msg_1/forward"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"to\""
      body shouldContain "\"fwd@example.com\""
      respondJson("""{"message_id": "msg_4", "thread_id": "thread_2"}""")
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.forward("msg_1") { to = listOf("fwd@example.com") }
    result.messageId shouldBe "msg_4"
  }

  "MessageResource.getRaw() sends GET to basePath/{id}/raw" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/msg_1/raw"
      respondJson("""{"raw": "MIME-Version: 1.0\r\nFrom: a@b.com"}""")
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.getRaw("msg_1")
    result.raw shouldContain "MIME-Version"
  }

  "MessageResource.update() sends PATCH to basePath/{id} with labels" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Patch
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/msg_1"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"labels\""
      body shouldContain "\"read\""
      respondJson(
        """
                {
                    "message_id": "msg_1",
                    "labels": ["read"]
                }
            """
      )
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.update("msg_1") { labels = listOf("read") }
    result.labels shouldBe listOf("read")
  }

  "MessageResource.getAttachment() sends GET to basePath/{id}/attachments/{attachmentId}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/msg_1/attachments/att_1"
      respond(
        content = "file-content-bytes",
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/pdf"),
      )
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.getAttachment("msg_1", "att_1")
    result.contentType shouldBe "application/pdf"
    result.data.decodeToString() shouldBe "file-content-bytes"
  }

  "DomainResource.getZoneFile() sends GET to basePath/{id}/zone-file" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/domains/domain_1/zone-file"
      respond(
        content = "zone-file-content",
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/octet-stream"),
      )
    }
    val resource = DomainResource(client, ApiPaths.DOMAINS)
    val result = resource.getZoneFile("domain_1")
    result.decodeToString() shouldBe "zone-file-content"
  }

  "DraftResource.send() sends POST to basePath/{id}/send" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/drafts/draft_1/send"
      respondJson("""{"message_id": "msg_5", "thread_id": "thread_3"}""")
    }
    val resource = DraftResource(client, ApiPaths.DRAFTS)
    val result = resource.send("draft_1")
    result.messageId shouldBe "msg_5"
  }

  "DraftResource.update() sends PATCH to basePath/{id}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Patch
      request.url.encodedPath shouldBe "/v0/drafts/draft_1"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"subject\""
      body shouldContain "\"Updated subject\""
      respondJson(
        """
                {
                    "inbox_id": "inbox_1",
                    "draft_id": "draft_1",
                    "subject": "Updated subject",
                    "timestamp": "2026-01-01T00:00:00Z",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = DraftResource(client, ApiPaths.DRAFTS)
    val result = resource.update("draft_1") { subject = "Updated subject" }
    result.subject shouldBe "Updated subject"
  }

  "ThreadResource.delete() sends DELETE with permanent query param" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Delete
      request.url.encodedPath shouldBe "/v0/threads/thread_1"
      request.url.parameters["permanent"] shouldBe "true"
      respondJson("{}")
    }
    val resource = ThreadResource(client, ApiPaths.THREADS)
    resource.delete("thread_1") { permanent = true }
  }

  "ListResource.list() constructs path from direction and type" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/lists/allow/sender"
      respondJson("""{"count": 0, "entries": []}""")
    }
    val resource = ListResource(client, ApiPaths.LISTS)
    val result = resource.list(ListDirection.ALLOW, ListType.SENDER)
    result.count shouldBe 0
  }

  "ListResource.get() constructs path from direction, type, and entry" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/lists/block/domain/spam.com"
      respondJson(
        """
                {
                    "entry": "spam.com",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = ListResource(client, ApiPaths.LISTS)
    val result = resource.get(ListDirection.BLOCK, ListType.DOMAIN, "spam.com")
    result.entry shouldBe "spam.com"
  }

  "ListResource.delete() sends DELETE to basePath/{direction}/{type}/{entry}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Delete
      request.url.encodedPath shouldBe "/v0/lists/block/subject/test@example.com"
      respondJson("{}")
    }
    val resource = ListResource(client, ApiPaths.LISTS)
    resource.delete(ListDirection.BLOCK, ListType.SUBJECT, "test@example.com")
  }

  // --- Additional InboxScope paths ---

  "InboxScope lists use correct base path v0/inboxes/{inboxId}/lists" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_99/lists/allow/sender"
      respondJson("""{"count": 0, "entries": []}""")
    }
    val scope = InboxScope(client, "inbox_99")
    val result = scope.lists.list(ListDirection.ALLOW, ListType.SENDER)
    result.count shouldBe 0
  }

  "InboxScope metrics use correct base path v0/inboxes/{inboxId}/metrics" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_99/metrics"
      respondJson("""{"metrics": []}""")
    }
    val scope = InboxScope(client, "inbox_99")
    val result = scope.metrics.query()
    result.metrics shouldBe emptyList()
  }

  "InboxScope apiKeys use correct base path v0/inboxes/{inboxId}/api-keys" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_99/api-keys"
      respondJson("""{"count": 0, "api_keys": []}""")
    }
    val scope = InboxScope(client, "inbox_99")
    val result = scope.apiKeys.list()
    result.count shouldBe 0
  }

  // --- Additional PodScope paths ---

  "PodScope drafts use correct base path v0/pods/{podId}/drafts" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/pods/pod_42/drafts"
      respondJson("""{"count": 0, "drafts": []}""")
    }
    val scope = PodScope(client, "pod_42")
    val result = scope.drafts.list()
    result.count shouldBe 0
  }

  "PodScope lists use correct base path v0/pods/{podId}/lists" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/pods/pod_42/lists/block/domain"
      respondJson("""{"count": 0, "entries": []}""")
    }
    val scope = PodScope(client, "pod_42")
    val result = scope.lists.list(ListDirection.BLOCK, ListType.DOMAIN)
    result.count shouldBe 0
  }

  "PodScope metrics use correct base path v0/pods/{podId}/metrics" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/pods/pod_42/metrics"
      respondJson("""{"metrics": []}""")
    }
    val scope = PodScope(client, "pod_42")
    val result = scope.metrics.query()
    result.metrics shouldBe emptyList()
  }

  "PodScope apiKeys use correct base path v0/pods/{podId}/api-keys" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/pods/pod_42/api-keys"
      respondJson("""{"count": 0, "api_keys": []}""")
    }
    val scope = PodScope(client, "pod_42")
    val result = scope.apiKeys.list()
    result.count shouldBe 0
  }

  "PodScope nested inboxes(id) returns InboxScope with correct path" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_77/messages"
      respondJson("""{"count": 0, "messages": []}""")
    }
    val scope = PodScope(client, "pod_42")
    val inboxScope = scope.inboxes("inbox_77")
    val result = inboxScope.messages.list()
    result.count shouldBe 0
  }

  // --- Additional resource operations ---

  "MessageResource.get() sends GET to basePath/{id}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages/msg_1"
      respondJson(
        """
                {
                    "inbox_id": "inbox_1",
                    "thread_id": "thread_1",
                    "message_id": "msg_1",
                    "timestamp": "2026-01-01T00:00:00Z",
                    "from": "a@b.com",
                    "size": 100,
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.get("msg_1")
    result.messageId shouldBe "msg_1"
  }

  "MessageResource.list() sends GET with query parameters" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/inboxes/inbox_1/messages"
      request.url.parameters["limit"] shouldBe "5"
      request.url.parameters["labels"] shouldBe "inbox,unread"
      request.url.parameters["ascending"] shouldBe "true"
      respondJson("""{"count": 0, "messages": []}""")
    }
    val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
    val result = resource.list {
      limit = 5
      labels = listOf("inbox", "unread")
      ascending = true
    }
    result.count shouldBe 0
  }

  "ThreadResource.get() sends GET to basePath/{id}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/threads/thread_1"
      respondJson(
        """
                {
                    "inbox_id": "inbox_1",
                    "thread_id": "thread_1",
                    "timestamp": "2026-01-01T00:00:00Z",
                    "subject": "Test",
                    "last_message_id": "msg_1",
                    "message_count": 1,
                    "size": 100,
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = ThreadResource(client, ApiPaths.THREADS)
    val result = resource.get("thread_1")
    result.threadId shouldBe "thread_1"
    result.subject shouldBe "Test"
  }

  "DraftResource.list() sends GET with query parameters" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/drafts"
      request.url.parameters["limit"] shouldBe "10"
      respondJson("""{"count": 0, "drafts": []}""")
    }
    val resource = DraftResource(client, ApiPaths.DRAFTS)
    val result = resource.list { limit = 10 }
    result.count shouldBe 0
  }

  "DraftResource.get() sends GET to basePath/{id}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/drafts/draft_1"
      respondJson(
        """
                {
                    "inbox_id": "inbox_1",
                    "draft_id": "draft_1",
                    "timestamp": "2026-01-01T00:00:00Z",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = DraftResource(client, ApiPaths.DRAFTS)
    val result = resource.get("draft_1")
    result.draftId shouldBe "draft_1"
  }

  "DraftResource.create() sends POST with body" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/drafts"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"to\""
      body shouldContain "\"alice@example.com\""
      respondJson(
        """
                {
                    "inbox_id": "inbox_1",
                    "draft_id": "draft_2",
                    "timestamp": "2026-01-01T00:00:00Z",
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = DraftResource(client, ApiPaths.DRAFTS)
    val result = resource.create { to = listOf("alice@example.com") }
    result.draftId shouldBe "draft_2"
  }

  "DomainResource.list() sends GET with pagination" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/domains"
      request.url.parameters["limit"] shouldBe "5"
      respondJson("""{"count": 0, "domains": []}""")
    }
    val resource = DomainResource(client, ApiPaths.DOMAINS)
    val result = resource.list { limit = 5 }
    result.count shouldBe 0
  }

  "DomainResource.create() sends POST with name" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Post
      request.url.encodedPath shouldBe "/v0/domains"
      val body = request.body.toByteArray().decodeToString()
      body shouldContain "\"name\""
      body shouldContain "\"example.com\""
      respondJson(
        """
                {
                    "domain_id": "domain_1",
                    "name": "example.com",
                    "verified": false,
                    "updated_at": "2026-01-01T00:00:00Z",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            """
      )
    }
    val resource = DomainResource(client, ApiPaths.DOMAINS)
    val result = resource.create { name = "example.com" }
    result.domainId shouldBe "domain_1"
    result.name shouldBe "example.com"
  }

  "DomainResource.delete() sends DELETE to basePath/{id}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Delete
      request.url.encodedPath shouldBe "/v0/domains/domain_1"
      respondJson("{}")
    }
    val resource = DomainResource(client, ApiPaths.DOMAINS)
    resource.delete("domain_1")
  }

  "WebhookResource.list() sends GET to basePath" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/webhooks"
      respondJson("""{"count": 0, "webhooks": []}""")
    }
    val resource = WebhookResource(client, ApiPaths.WEBHOOKS)
    val result = resource.list()
    result.count shouldBe 0
  }

  "WebhookResource.delete() sends DELETE to basePath/{id}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Delete
      request.url.encodedPath shouldBe "/v0/webhooks/wh_1"
      respondJson("{}")
    }
    val resource = WebhookResource(client, ApiPaths.WEBHOOKS)
    resource.delete("wh_1")
  }

  "PodResource.list() sends GET with query parameters" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/pods"
      request.url.parameters["limit"] shouldBe "10"
      respondJson("""{"count": 0, "pods": []}""")
    }
    val resource = PodResource(client, ApiPaths.PODS)
    val result = resource.list { limit = 10 }
    result.count shouldBe 0
  }

  "PodResource.delete() sends DELETE to basePath/{id}" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Delete
      request.url.encodedPath shouldBe "/v0/pods/pod_1"
      respondJson("{}")
    }
    val resource = PodResource(client, ApiPaths.PODS)
    resource.delete("pod_1")
  }

  "MetricsResource.query() sends GET with builder params" {
    val client = mockClient { request ->
      request.method shouldBe HttpMethod.Get
      request.url.encodedPath shouldBe "/v0/metrics"
      request.url.parameters["event_types"] shouldBe "sent,received"
      request.url.parameters["period"] shouldBe "day"
      respondJson("""{"metrics": []}""")
    }
    val resource = MetricsResource(client, ApiPaths.METRICS)
    val result = resource.query {
      eventTypes = "sent,received"
      period = com.agentmail4k.sdk.model.MetricsPeriod.DAY
    }
    result.metrics shouldBe emptyList()
  }
})
