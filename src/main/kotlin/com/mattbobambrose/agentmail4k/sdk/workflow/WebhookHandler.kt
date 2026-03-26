package com.mattbobambrose.agentmail4k.sdk.workflow

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl
import com.mattbobambrose.agentmail4k.sdk.model.WebhookEvent
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@AgentMailDsl
class WebhookHandlerBuilder {
  var signingSecret: String? = null
  private val handlers = mutableMapOf<String, suspend (JsonObject) -> Unit>()

  fun on(event: WebhookEvent, handler: suspend (JsonObject) -> Unit) {
    handlers[event.value] = handler
  }

  fun onMessageReceived(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_RECEIVED, handler)
  fun onMessageSent(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_SENT, handler)
  fun onMessageDelivered(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_DELIVERED, handler)
  fun onMessageBounced(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_BOUNCED, handler)
  fun onMessageComplained(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_COMPLAINED, handler)
  fun onMessageRejected(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.MESSAGE_REJECTED, handler)
  fun onDomainVerified(handler: suspend (JsonObject) -> Unit) = on(WebhookEvent.DOMAIN_VERIFIED, handler)

  internal fun build() = WebhookHandler(
    signingSecret = signingSecret,
    handlers = handlers.toMap(),
  )
}

class WebhookHandler internal constructor(
  private val signingSecret: String?,
  private val handlers: Map<String, suspend (JsonObject) -> Unit>,
) {
  private val json = Json { ignoreUnknownKeys = true }

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

  suspend fun handle(headers: Map<String, String>, body: String): Boolean {
    if (!verify(headers, body)) return false

    val payload = json.decodeFromString<JsonObject>(body)
    val eventType = payload["type"]?.jsonPrimitive?.content ?: return false
    val handler = handlers[eventType] ?: return true

    handler(payload)
    return true
  }
}

fun webhookHandler(block: WebhookHandlerBuilder.() -> Unit): WebhookHandler {
  return WebhookHandlerBuilder().apply(block).build()
}
