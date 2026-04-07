package com.agentmail4k.sdk

import com.agentmail4k.sdk.internal.SlidingWindowRateLimiter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class RateLimiterTest : StringSpec() {
  init {
    "DELAY: acquire does not delay when under limit" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 2, window = 200.milliseconds, onLimitExceeded = RateLimitAction.DELAY))
      val elapsed = measureTime {
        limiter.acquire("key1") shouldBe true
      }
      elapsed.inWholeMilliseconds shouldBeLessThan 50
    }

    "DELAY: acquire suspends when limit is reached" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 1, window = 200.milliseconds, onLimitExceeded = RateLimitAction.DELAY))
      limiter.acquire("key1")
      val elapsed = measureTime {
        limiter.acquire("key1") shouldBe true
      }
      elapsed.inWholeMilliseconds shouldBeGreaterThanOrEqual 150
    }

    "DELAY: different keys are independent" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 1, window = 500.milliseconds, onLimitExceeded = RateLimitAction.DELAY))
      limiter.acquire("key1")
      val elapsed = measureTime {
        limiter.acquire("key2") shouldBe true
      }
      elapsed.inWholeMilliseconds shouldBeLessThan 50
    }

    "DELAY: concurrent acquire on same key serializes correctly" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 1, window = 100.milliseconds, onLimitExceeded = RateLimitAction.DELAY))
      val elapsed = measureTime {
        (1..3).map { async { limiter.acquire("key1") } }.awaitAll()
      }
      elapsed.inWholeMilliseconds shouldBeGreaterThanOrEqual 150
    }

    "DELAY: allows burst up to maxMessages before throttling" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 3, window = 300.milliseconds, onLimitExceeded = RateLimitAction.DELAY))
      val burstElapsed = measureTime {
        repeat(3) { limiter.acquire("key1") }
      }
      burstElapsed.inWholeMilliseconds shouldBeLessThan 50

      val throttledElapsed = measureTime {
        limiter.acquire("key1")
      }
      throttledElapsed.inWholeMilliseconds shouldBeGreaterThanOrEqual 200
    }

    "SKIP: returns false when limit is exceeded" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 1, window = 500.milliseconds, onLimitExceeded = RateLimitAction.SKIP))
      limiter.acquire("key1") shouldBe true
      limiter.acquire("key1") shouldBe false
    }

    "SKIP: does not delay" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 1, window = 500.milliseconds, onLimitExceeded = RateLimitAction.SKIP))
      limiter.acquire("key1")
      val elapsed = measureTime {
        limiter.acquire("key1")
      }
      elapsed.inWholeMilliseconds shouldBeLessThan 50
    }

    "STOP: throws RateLimitExceededException when limit is exceeded" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 1, window = 500.milliseconds, onLimitExceeded = RateLimitAction.STOP))
      limiter.acquire("key1") shouldBe true
      val ex = shouldThrow<RateLimitExceededException> {
        limiter.acquire("key1")
      }
      ex.key shouldBe "key1"
      ex.maxMessages shouldBe 1
      ex.window shouldBe 500.milliseconds
    }

    "STOP: does not throw when under limit" {
      val limiter = SlidingWindowRateLimiter(RateLimiterConfig(maxMessages = 2, window = 500.milliseconds, onLimitExceeded = RateLimitAction.STOP))
      limiter.acquire("key1") shouldBe true
      limiter.acquire("key1") shouldBe true
    }

    "config: perSenderRateLimiter is null by default" {
      val config = AgentMailConfigBuilder().apply { apiKey = "test" }.build()
      config.perSenderRateLimiter shouldBe null
    }

    "config: perRecipientRateLimiter is null by default" {
      val config = AgentMailConfigBuilder().apply { apiKey = "test" }.build()
      config.perRecipientRateLimiter shouldBe null
    }

    "config: perSenderRateLimiter accepts custom values" {
      val config = AgentMailConfigBuilder().apply {
        apiKey = "test"
        perSenderRateLimiter {
          maxMessages = 5
          window = 10.seconds
          onLimitExceeded = RateLimitAction.DELAY
        }
      }.build()
      config.perSenderRateLimiter shouldNotBe null
      config.perSenderRateLimiter!!.maxMessages shouldBe 5
      config.perSenderRateLimiter.window shouldBe 10.seconds
      config.perSenderRateLimiter.onLimitExceeded shouldBe RateLimitAction.DELAY
    }

    "config: perRecipientRateLimiter accepts custom values" {
      val config = AgentMailConfigBuilder().apply {
        apiKey = "test"
        perRecipientRateLimiter {
          maxMessages = 1
          window = 5.seconds
        }
      }.build()
      config.perRecipientRateLimiter shouldNotBe null
      config.perRecipientRateLimiter!!.maxMessages shouldBe 1
      config.perRecipientRateLimiter.window shouldBe 5.seconds
      config.perRecipientRateLimiter.onLimitExceeded shouldBe RateLimitAction.STOP
    }
  }
}
