package com.agentmail4k.sdk

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import com.agentmail4k.sdk.model.Message
import com.agentmail4k.sdk.model.Thread
import com.agentmail4k.sdk.resource.InboxScope
import com.agentmail4k.sdk.resource.MessageResource
import com.agentmail4k.sdk.resource.ThreadResource
import io.mockk.every
import io.mockk.mockk

internal val testInstant: Instant = Instant.parse("2026-01-01T00:00:00Z")

internal fun testMessage(
  messageId: String = "msg_1",
  inboxId: String = "inbox_1",
  threadId: String = "thread_1",
  from: String = "sender@example.com",
  subject: String = "Test",
) = Message(
  inboxId = inboxId,
  threadId = threadId,
  messageId = messageId,
  timestamp = testInstant,
  from = from,
  subject = subject,
  size = 100,
  updatedAt = testInstant,
  createdAt = testInstant,
)

internal val testJson = Json {
  ignoreUnknownKeys = true
  encodeDefaults = false
  explicitNulls = false
}

internal fun mockClient(
  handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
): HttpClient = HttpClient(MockEngine { request -> handler(request) }) {
  install(ContentNegotiation) { json(testJson) }
  defaultRequest {
    url("https://api.agentmail.to/")
    contentType(ContentType.Application.Json)
  }
}

internal fun dummyClient(): HttpClient = mockClient { respondJson("{}") }

internal fun testThread(
  threadId: String = "thread_1",
  inboxId: String = "inbox_1",
  subject: String = "Test",
) = Thread(
  inboxId = inboxId,
  threadId = threadId,
  timestamp = testInstant,
  subject = subject,
  lastMessageId = "msg_1",
  messageCount = 1,
  size = 100,
  updatedAt = testInstant,
  createdAt = testInstant,
)

internal fun mockInboxClient(inboxId: String = "inbox_1"): Pair<AgentMailClient, MessageResource> {
  val mockMessages = mockk<MessageResource>()
  val mockScope = mockk<InboxScope> {
    every { messages } returns mockMessages
  }
  val client = mockk<AgentMailClient>(relaxUnitFun = true) {
    every { inboxes(inboxId) } returns mockScope
  }
  return client to mockMessages
}

internal fun mockThreadClient(inboxId: String = "inbox_1"): Pair<AgentMailClient, ThreadResource> {
  val mockThreads = mockk<ThreadResource>()
  val mockScope = mockk<InboxScope> {
    every { threads } returns mockThreads
  }
  val client = mockk<AgentMailClient>(relaxUnitFun = true) {
    every { inboxes(inboxId) } returns mockScope
  }
  return client to mockThreads
}

internal fun MockRequestHandleScope.respondJson(json: String): HttpResponseData =
  respond(
    content = json.trimIndent(),
    status = HttpStatusCode.OK,
    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
  )
