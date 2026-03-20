package to.agentmail.sdk

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AgentMailDsl
class AgentMailConfigBuilder {
    var apiKey: String? = null
    var baseUrl: String = "https://api.agentmail.to"
    private var timeoutBuilder = TimeoutBuilder()
    private var retryBuilder = RetryBuilder()

    fun timeout(block: TimeoutBuilder.() -> Unit) {
        timeoutBuilder.apply(block)
    }

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

data class AgentMailConfig(
    val apiKey: String,
    val baseUrl: String,
    val timeout: TimeoutConfig,
    val retry: RetryConfig,
)

@AgentMailDsl
class TimeoutBuilder {
    var connect: Duration = 10.seconds
    var request: Duration = 30.seconds
    var socket: Duration = 30.seconds

    internal fun build() = TimeoutConfig(connect, request, socket)
}

data class TimeoutConfig(
    val connect: Duration,
    val request: Duration,
    val socket: Duration,
)

@AgentMailDsl
class RetryBuilder {
    var maxRetries: Int = 3
    var retryOnServerErrors: Boolean = true

    internal fun build() = RetryConfig(maxRetries, retryOnServerErrors)
}

data class RetryConfig(
    val maxRetries: Int,
    val retryOnServerErrors: Boolean,
)
