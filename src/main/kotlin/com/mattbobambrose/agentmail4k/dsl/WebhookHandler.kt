package com.mattbobambrose.agentmail4k.dsl

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl
import com.mattbobambrose.agentmail4k.sdk.model.WebhookEvent
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/** DSL builder for configuring webhook event handlers with optional signature verification. */
@AgentMailDsl
class WebhookHandlerBuilder {
  var signingSecret: String? = null
  private val handlers = mutableMapOf<String, suspend (JsonObject) -> Unit>()

  /** Registers a handler for a specific webhook [event]. */
  fun on(event: WebhookEvent, handler: suspend (JsonObject) -> Unit) {
    handlers[event.value] = handler
  }

  /** Registers a handler for the message.received event. */
  fun onMessageReceived(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_RECEIVED, handler)
  /** Registers a handler for the message.sent event. */
  fun onMessageSent(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_SENT, handler)
  /** Registers a handler for the message.delivered event. */
  fun onMessageDelivered(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_DELIVERED, handler)
  /** Registers a handler for the message.bounced event. */
  fun onMessageBounced(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_BOUNCED, handler)
  /** Registers a handler for the message.complained event. */
  fun onMessageComplained(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_COMPLAINED, handler)
  /** Registers a handler for the message.rejected event. */
  fun onMessageRejected(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_REJECTED, handler)
  /** Registers a handler for the domain.verified event. */
  fun onDomainVerified(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.DOMAIN_VERIFIED, handler)

  internal fun build() = WebhookHandler(
    signingSecret = signingSecret,
    handlers = handlers.toMap(),
  )
}

/** Processes incoming webhook payloads with optional HMAC-SHA256 signature verification and event-specific handlers. */
class WebhookHandler internal constructor(
  private val signingSecret: String?,
  private val handlers: Map<String, suspend (JsonObject) -> Unit>,
) {
  private val json = Json { ignoreUnknownKeys = true }

  /** Verifies the HMAC-SHA256 signature of a webhook payload. Returns true if valid or if no signing secret is configured. */
  fun verify(headers: Map<String, String>, body: String): Boolean {
    val secret = signingSecret ?: return true
    val svixId = headers["svix-id"] ?: return false
    val svixTimestamp = headers["svix-timestamp"] ?: return false
    val svixSignature = headers["svix-signature"] ?: return false

    val toSign = "$svixId.$svixTimestamp.$body"
    val secretBytes = Base64.getDecoder().decode(secret.removePrefix("whsec_"))
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(secretBytes, "HmacSHA256"))
    val expectedSignature = Base64.getEncoder().encodeToString(mac.doFinal(toSign.toByteArray()))

    return svixSignature.split(" ").any { sig ->
      val sigValue = sig.removePrefix("v1,")
      sigValue == expectedSignature
    }
  }

  /** Verifies and dispatches a webhook payload to the appropriate event handler. Returns false if signature verification fails. */
  suspend fun handle(headers: Map<String, String>, body: String): Boolean {
    if (!verify(headers, body)) return false

    val payload = json.decodeFromString<JsonObject>(body)
    val eventType = payload["type"]?.jsonPrimitive?.content ?: return false
    val handler = handlers[eventType] ?: return true

    handler(payload)
    return true
  }
}

/** Creates a [WebhookHandler] using the DSL builder. */
fun webhookHandler(block: WebhookHandlerBuilder.() -> Unit): WebhookHandler {
  return WebhookHandlerBuilder().apply(block).build()
}
