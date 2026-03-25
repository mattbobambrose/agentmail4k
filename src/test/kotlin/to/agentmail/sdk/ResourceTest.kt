package to.agentmail.sdk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import to.agentmail.sdk.internal.ApiPaths
import to.agentmail.sdk.resource.ApiKeyResource
import to.agentmail.sdk.resource.DomainResource
import to.agentmail.sdk.resource.InboxResource
import to.agentmail.sdk.resource.InboxScope
import to.agentmail.sdk.resource.MessageResource
import to.agentmail.sdk.resource.OrganizationResource
import to.agentmail.sdk.resource.PodResource
import to.agentmail.sdk.resource.PodScope
import to.agentmail.sdk.resource.ThreadResource
import to.agentmail.sdk.resource.WebhookResource

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
}) {
  companion object {
    private fun mockClient(
      handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): HttpClient {
      return HttpClient(MockEngine { request -> handler(request) }) {
        install(ContentNegotiation) {
          json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
            explicitNulls = false
          })
        }
        defaultRequest {
          url("https://api.agentmail.to/")
          contentType(ContentType.Application.Json)
        }
      }
    }

    private fun MockRequestHandleScope.respondJson(json: String): HttpResponseData {
      return respond(
        content = json.trimIndent(),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
      )
    }
  }
}
