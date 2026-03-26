package com.mattbobambrose.agentmail4k.sdk

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
import kotlinx.serialization.json.Json

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

internal fun MockRequestHandleScope.respondJson(json: String): HttpResponseData =
  respond(
    content = json.trimIndent(),
    status = HttpStatusCode.OK,
    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
  )
