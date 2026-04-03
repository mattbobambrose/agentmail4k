package com.agentmail4k.dsl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.model.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Simplified inbox polling that invokes a handler for each new message at a fixed interval. */
fun AgentMailClient.poll(
  inboxId: String,
  interval: Duration = 10.seconds,
  filter: ((Message) -> Boolean)? = null,
  scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
  handler: suspend (Message) -> Unit,
): Job {
  return monitor(inboxId, scope) {
    pollInterval = interval
    filter?.let { filterBy(it) }
    onMessage(handler = handler)
  }
}
