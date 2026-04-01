@file:Suppress("unused")

package website

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// --8<-- [start:full-config]
suspend fun fullConfig() {
    val client = AgentMailClient {
        apiKey = "your-api-key"
        baseUrl = "https://api.agentmail.to"

        timeout {
            connect = 15.seconds
            request = 1.minutes
            socket = 1.minutes
        }

        retry {
            maxRetries = 5
            retryOnServerErrors = true
        }
    }
    // ... use the client
    client.close()
}
// --8<-- [end:full-config]

// --8<-- [start:timeout-config]
suspend fun timeoutConfig() {
    val client = AgentMailClient {
        timeout {
            connect = 5.seconds   // Time to establish connection
            request = 30.seconds  // Total request timeout
            socket = 30.seconds   // Time between data packets
        }
    }
    client.close()
}
// --8<-- [end:timeout-config]

// --8<-- [start:retry-config]
suspend fun retryConfig() {
    val client = AgentMailClient {
        retry {
            maxRetries = 3              // Number of retry attempts
            retryOnServerErrors = true  // Retry on 5xx errors
        }
    }
    client.close()
}
// --8<-- [end:retry-config]

// --8<-- [start:defaults]
suspend fun defaults() {
    // All defaults are applied automatically:
    //   apiKey       = AGENTMAIL_API_KEY environment variable
    //   baseUrl      = "https://api.agentmail.to"
    //   connect      = 10 seconds
    //   request      = 30 seconds
    //   socket       = 30 seconds
    //   maxRetries   = 3
    //   retryOnServerErrors = true
    val client = AgentMailClient()
    client.close()
}
// --8<-- [end:defaults]
