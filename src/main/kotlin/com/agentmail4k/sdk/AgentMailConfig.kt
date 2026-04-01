package com.agentmail4k.sdk

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** DSL builder for configuring an [AgentMailClient] instance. Allows setting the API key, base URL, timeout, and retry settings. */
@AgentMailDsl
class AgentMailConfigBuilder {
  var apiKey: String? = null
  var baseUrl: String = "https://api.agentmail.to"
  private var timeoutBuilder = TimeoutBuilder()
  private var retryBuilder = RetryBuilder()

  /** Configures HTTP timeout settings. */
  fun timeout(block: TimeoutBuilder.() -> Unit) {
    timeoutBuilder.apply(block)
  }

  /** Configures HTTP retry settings. */
  fun retry(block: RetryBuilder.() -> Unit) {
    retryBuilder.apply(block)
  }

  internal fun build(): AgentMailConfig = AgentMailConfig(
    apiKey = apiKey ?: System.getenv("AGENTMAIL_API_KEY")
    ?: error("API key must be provided via constructor or AGENTMAIL_API_KEY environment variable"),
    baseUrl = baseUrl.trimEnd('/'),
    timeout = timeoutBuilder.build(),
    retry = retryBuilder.build(),
  )
}

/** Immutable configuration for the AgentMail HTTP client. */
data class AgentMailConfig(
  val apiKey: String,
  val baseUrl: String,
  val timeout: TimeoutConfig,
  val retry: RetryConfig,
)

/** DSL builder for configuring HTTP timeout durations. */
@AgentMailDsl
class TimeoutBuilder {
  var connect: Duration = 10.seconds
  var request: Duration = 30.seconds
  var socket: Duration = 30.seconds

  internal fun build() = TimeoutConfig(connect, request, socket)
}

/** Immutable timeout configuration with connect, request, and socket durations. */
data class TimeoutConfig(
  val connect: Duration,
  val request: Duration,
  val socket: Duration,
)

/** DSL builder for configuring HTTP retry behavior. */
@AgentMailDsl
class RetryBuilder {
  var maxRetries: Int = 3
  var retryOnServerErrors: Boolean = true

  internal fun build() = RetryConfig(maxRetries, retryOnServerErrors)
}

/** Immutable retry configuration with max retries and server error retry toggle. */
data class RetryConfig(
  val maxRetries: Int,
  val retryOnServerErrors: Boolean,
)
