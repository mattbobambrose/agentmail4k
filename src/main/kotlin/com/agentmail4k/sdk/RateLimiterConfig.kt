package com.agentmail4k.sdk

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Action to take when a rate limit is exceeded. */
enum class RateLimitAction {
  /** Suspend until the window clears, then send. */
  DELAY,
  /** Log a warning and skip the message without sending. */
  SKIP,
  /** Log an error and throw [RateLimitExceededException]. */
  STOP,
}

/** Thrown when a rate limit is exceeded and the action is [RateLimitAction.STOP]. */
class RateLimitExceededException(
  val key: String,
  val maxMessages: Int,
  val window: Duration,
) : RuntimeException("Rate limit exceeded for '$key' ($maxMessages per $window)")

/** DSL builder for configuring a per-key rate limiter with max messages and time window. */
@AgentMailDsl
class RateLimiterBuilder {
  var maxMessages: Int = 1
  var window: Duration = 5.seconds
  var onLimitExceeded: RateLimitAction = RateLimitAction.STOP

  internal fun build() = RateLimiterConfig(maxMessages, window, onLimitExceeded)
}

/** Immutable rate limiter configuration with max messages allowed within a sliding time window. */
data class RateLimiterConfig(
  val maxMessages: Int,
  val window: Duration,
  val onLimitExceeded: RateLimitAction = RateLimitAction.STOP,
)
