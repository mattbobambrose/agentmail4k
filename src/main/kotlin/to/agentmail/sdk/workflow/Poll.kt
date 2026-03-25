package to.agentmail.sdk.workflow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import to.agentmail.sdk.AgentMailClient
import to.agentmail.sdk.model.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
