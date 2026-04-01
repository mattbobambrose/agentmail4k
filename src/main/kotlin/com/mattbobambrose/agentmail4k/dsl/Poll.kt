package com.mattbobambrose.agentmail4k.dsl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.model.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Simplified inbox polling that invokes a handler for each new message at a fixed interval. */
fun AgentMailClient.poll(
  inboxId: String,
  interval: Duration = 10.seconds,
  scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
  handler: suspend (Message) -> Unit,
): Job {
  return monitor(inboxId, scope) {
    pollInterval = interval
    onMessage(handler = handler)
  }
}
