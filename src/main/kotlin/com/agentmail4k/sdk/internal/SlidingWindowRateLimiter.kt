package com.agentmail4k.sdk.internal

import com.agentmail4k.sdk.RateLimitAction
import com.agentmail4k.sdk.RateLimitExceededException
import com.agentmail4k.sdk.RateLimiterConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory
import kotlin.time.TimeSource

private val logger = LoggerFactory.getLogger(SlidingWindowRateLimiter::class.java)

/**
 * Per-key sliding window rate limiter. Tracks timestamps of recent acquires per key
 * and suspends callers when the limit is reached until the window clears.
 * Coroutine-safe via per-key [Mutex].
 */
internal class SlidingWindowRateLimiter(private val config: RateLimiterConfig) {
  private class KeyState(
    val timestamps: ArrayDeque<TimeSource.Monotonic.ValueTimeMark> = ArrayDeque(),
    val mutex: Mutex = Mutex(),
  )

  private val keys = ConcurrentHashMap<String, KeyState>()

  /** Returns `true` if the caller should proceed, `false` if the message should be skipped. */
  suspend fun acquire(key: String): Boolean {
    val state = keys.getOrPut(key) { KeyState() }
    state.mutex.withLock {
      evict(state)
      if (state.timestamps.size >= config.maxMessages) {
        when (config.onLimitExceeded) {
          RateLimitAction.DELAY -> {
            val oldest = state.timestamps.first()
            val waitTime = config.window - oldest.elapsedNow()
            if (waitTime.isPositive()) {
              logger.warn("Rate limit exceeded for '{}' ({} per {}), delaying {}ms", key, config.maxMessages, config.window, waitTime.inWholeMilliseconds)
              delay(waitTime)
            }
            evict(state)
          }
          RateLimitAction.SKIP -> {
            logger.warn("Rate limit exceeded for '{}' ({} per {}), skipping message", key, config.maxMessages, config.window)
            return false
          }
          RateLimitAction.STOP -> {
            logger.error("Rate limit exceeded for '{}' ({} per {}), stopping", key, config.maxMessages, config.window)
            throw RateLimitExceededException(key, config.maxMessages, config.window)
          }
        }
      }
      state.timestamps.addLast(TimeSource.Monotonic.markNow())
      return true
    }
  }

  private fun evict(state: KeyState) {
    while (state.timestamps.isNotEmpty() && state.timestamps.first().elapsedNow() >= config.window) {
      state.timestamps.removeFirst()
    }
  }
}
