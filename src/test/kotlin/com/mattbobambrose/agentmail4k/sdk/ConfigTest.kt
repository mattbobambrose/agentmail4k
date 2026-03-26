package com.mattbobambrose.agentmail4k.sdk

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.time.Duration.Companion.seconds

class ConfigTest : StringSpec() {
  init {
    "build() should throw when apiKey is null and env var is unset" {
      val builder = AgentMailConfigBuilder().apply {
        apiKey = null
      }
      shouldThrow<IllegalStateException> {
        builder.build()
      }.message shouldContain "API key must be provided"
    }

    "build() should trim trailing slash from baseUrl" {
      val builder = AgentMailConfigBuilder().apply {
        apiKey = "test-key"
        baseUrl = "https://example.com/"
      }
      val config = builder.build()
      config.baseUrl shouldBe "https://example.com"
    }

    "build() should trim multiple trailing slashes from baseUrl" {
      val builder = AgentMailConfigBuilder().apply {
        apiKey = "test-key"
        baseUrl = "https://example.com///"
      }
      val config = builder.build()
      config.baseUrl shouldBe "https://example.com"
    }

    "timeout builder should use default values" {
      val builder = AgentMailConfigBuilder().apply {
        apiKey = "test-key"
      }
      val config = builder.build()
      config.timeout.connect shouldBe 10.seconds
      config.timeout.request shouldBe 30.seconds
      config.timeout.socket shouldBe 30.seconds
    }

    "retry builder should use default values" {
      val builder = AgentMailConfigBuilder().apply {
        apiKey = "test-key"
      }
      val config = builder.build()
      config.retry.maxRetries shouldBe 3
      config.retry.retryOnServerErrors shouldBe true
    }

    "timeout builder should accept custom values" {
      val builder = AgentMailConfigBuilder().apply {
        apiKey = "test-key"
        timeout {
          connect = 5.seconds
          request = 60.seconds
          socket = 15.seconds
        }
      }
      val config = builder.build()
      config.timeout.connect shouldBe 5.seconds
      config.timeout.request shouldBe 60.seconds
      config.timeout.socket shouldBe 15.seconds
    }

    "retry builder should accept custom values" {
      val builder = AgentMailConfigBuilder().apply {
        apiKey = "test-key"
        retry {
          maxRetries = 5
          retryOnServerErrors = false
        }
      }
      val config = builder.build()
      config.retry.maxRetries shouldBe 5
      config.retry.retryOnServerErrors shouldBe false
    }
  }
}
