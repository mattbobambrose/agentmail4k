package com.agentmail4k.sdk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonObject
import com.agentmail4k.sdk.model.WebhookEvent
import com.agentmail4k.dsl.webhookHandler
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class WorkflowTest : StringSpec({

  val secret = "whsec_" + Base64.getEncoder().encodeToString("test-secret".toByteArray())
  val svixId = "msg_test123"
  val svixTimestamp = "1234567890"
  val body = """{"type":"message.received","data":{}}"""
  val toSign = "$svixId.$svixTimestamp.$body"
  val secretBytes = Base64.getDecoder().decode(secret.removePrefix("whsec_"))
  val mac = Mac.getInstance("HmacSHA256").apply {
    init(SecretKeySpec(secretBytes, "HmacSHA256"))
  }
  val signature = "v1," + Base64.getEncoder().encodeToString(mac.doFinal(toSign.toByteArray()))

  fun validHeaders(sig: String = signature) = mapOf(
    "svix-id" to svixId,
    "svix-timestamp" to svixTimestamp,
    "svix-signature" to sig,
  )

  // --- WebhookHandler verification ---

  "verify returns true when signature is valid" {
    val handler = webhookHandler { signingSecret = secret }
    handler.verify(validHeaders(), body) shouldBe true
  }

  "verify returns false when signature is invalid" {
    val handler = webhookHandler { signingSecret = secret }
    handler.verify(validHeaders("v1,aW52YWxpZHNpZw=="), body) shouldBe false
  }

  "verify returns true when no signing secret is configured" {
    val handler = webhookHandler { }
    handler.verify(emptyMap(), body) shouldBe true
  }

  "verify returns false when svix-id header is missing" {
    val handler = webhookHandler { signingSecret = secret }
    val headers = mapOf(
      "svix-timestamp" to svixTimestamp,
      "svix-signature" to signature,
    )
    handler.verify(headers, body) shouldBe false
  }

  "verify returns false when svix-timestamp header is missing" {
    val handler = webhookHandler { signingSecret = secret }
    val headers = mapOf(
      "svix-id" to svixId,
      "svix-signature" to signature,
    )
    handler.verify(headers, body) shouldBe false
  }

  "verify returns false when svix-signature header is missing" {
    val handler = webhookHandler { signingSecret = secret }
    val headers = mapOf(
      "svix-id" to svixId,
      "svix-timestamp" to svixTimestamp,
    )
    handler.verify(headers, body) shouldBe false
  }

  // --- WebhookHandler dispatch ---

  "handle dispatches to the correct event handler based on type field" {
    var receivedPayload: JsonObject? = null
    val handler = webhookHandler {
      signingSecret = secret
      onMessageReceived { payload -> receivedPayload = payload }
    }

    val result = handler.handle(validHeaders(), body)
    result shouldBe true
    receivedPayload shouldBe kotlinx.serialization.json.Json.decodeFromString<JsonObject>(body)
  }

  "handle returns false when verification fails" {
    var called = false
    val handler = webhookHandler {
      signingSecret = secret
      onMessageReceived { called = true }
    }

    val result = handler.handle(validHeaders("v1,aW52YWxpZHNpZw=="), body)
    result shouldBe false
    called shouldBe false
  }

  "handle returns true when no handler matches the event type" {
    val handler = webhookHandler {
      signingSecret = secret
      onMessageSent { }
    }

    val result = handler.handle(validHeaders(), body)
    result shouldBe true
  }

  // --- WebhookHandler builder DSL ---

  "webhookHandler DSL creates handler with correct event registrations" {
    var messageReceivedCalled = false
    var messageSentCalled = false

    val handler = webhookHandler {
      signingSecret = secret
      onMessageReceived { messageReceivedCalled = true }
      onMessageSent { messageSentCalled = true }
    }

    handler.handle(validHeaders(), body)
    messageReceivedCalled shouldBe true
    messageSentCalled shouldBe false
  }

  "onMessageSent convenience method registers for message.sent event" {
    var called = false
    val sentBody = """{"type":"message.sent","data":{}}"""
    val sentToSign = "$svixId.$svixTimestamp.$sentBody"
    val sentMac = Mac.getInstance("HmacSHA256").apply {
      init(SecretKeySpec(secretBytes, "HmacSHA256"))
    }
    val sentSignature = "v1," + Base64.getEncoder().encodeToString(sentMac.doFinal(sentToSign.toByteArray()))

    val handler = webhookHandler {
      signingSecret = secret
      onMessageSent { called = true }
    }

    handler.handle(validHeaders(sentSignature), sentBody)
    called shouldBe true
  }

  "onMessageBounced convenience method registers for message.bounced event" {
    var called = false
    val bouncedBody = """{"type":"message.bounced","data":{}}"""
    val bouncedToSign = "$svixId.$svixTimestamp.$bouncedBody"
    val bouncedMac = Mac.getInstance("HmacSHA256").apply {
      init(SecretKeySpec(secretBytes, "HmacSHA256"))
    }
    val bouncedSignature = "v1," + Base64.getEncoder().encodeToString(bouncedMac.doFinal(bouncedToSign.toByteArray()))

    val handler = webhookHandler {
      signingSecret = secret
      onMessageBounced { called = true }
    }

    handler.handle(validHeaders(bouncedSignature), bouncedBody)
    called shouldBe true
  }

  "onDomainVerified convenience method registers for domain.verified event" {
    var called = false
    val domainBody = """{"type":"domain.verified","data":{}}"""
    val domainToSign = "$svixId.$svixTimestamp.$domainBody"
    val domainMac = Mac.getInstance("HmacSHA256").apply {
      init(SecretKeySpec(secretBytes, "HmacSHA256"))
    }
    val domainSignature = "v1," + Base64.getEncoder().encodeToString(domainMac.doFinal(domainToSign.toByteArray()))

    val handler = webhookHandler {
      signingSecret = secret
      onDomainVerified { called = true }
    }

    handler.handle(validHeaders(domainSignature), domainBody)
    called shouldBe true
  }

  "on method registers handler for arbitrary WebhookEvent" {
    var called = false
    val rejectedBody = """{"type":"message.rejected","data":{}}"""
    val rejectedToSign = "$svixId.$svixTimestamp.$rejectedBody"
    val rejectedMac = Mac.getInstance("HmacSHA256").apply {
      init(SecretKeySpec(secretBytes, "HmacSHA256"))
    }
    val rejectedSignature =
      "v1," + Base64.getEncoder().encodeToString(rejectedMac.doFinal(rejectedToSign.toByteArray()))

    val handler = webhookHandler {
      signingSecret = secret
      on(WebhookEvent.MESSAGE_REJECTED) { called = true }
    }

    handler.handle(validHeaders(rejectedSignature), rejectedBody)
    called shouldBe true
  }

  "handle returns false when payload has no type field" {
    val noTypeBody = """{"data":{}}"""
    val noTypeToSign = "$svixId.$svixTimestamp.$noTypeBody"
    val noTypeMac = Mac.getInstance("HmacSHA256").apply {
      init(SecretKeySpec(secretBytes, "HmacSHA256"))
    }
    val noTypeSignature = "v1," + Base64.getEncoder().encodeToString(noTypeMac.doFinal(noTypeToSign.toByteArray()))

    val handler = webhookHandler {
      signingSecret = secret
      onMessageReceived { }
    }

    val result = handler.handle(validHeaders(noTypeSignature), noTypeBody)
    result shouldBe false
  }

  "handle returns true for unregistered event type without signing secret" {
    val unknownBody = """{"type":"unknown.event","data":{}}"""
    var called = false
    val handler = webhookHandler {
      onMessageReceived { called = true }
    }

    val result = handler.handle(emptyMap(), unknownBody)
    result shouldBe true
    called shouldBe false
  }

  "handle returns false when body is empty JSON object" {
    val emptyBody = """{}"""
    val handler = webhookHandler { }

    val result = handler.handle(emptyMap(), emptyBody)
    result shouldBe false
  }
})
