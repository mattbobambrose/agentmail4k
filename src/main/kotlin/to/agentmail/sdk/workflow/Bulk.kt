package to.agentmail.sdk.workflow

import to.agentmail.sdk.AgentMail
import to.agentmail.sdk.AgentMailDsl
import to.agentmail.sdk.builder.ListThreadsBuilder
import to.agentmail.sdk.builder.SendMessageBuilder
import to.agentmail.sdk.model.SendMessageResponse
import to.agentmail.sdk.model.Thread

@AgentMailDsl
class BulkBuilder internal constructor(private val client: AgentMail) {
    private val operations = mutableListOf<suspend () -> Unit>()
    private val sendResults = mutableListOf<SendMessageResponse>()

    fun send(
        inboxId: String,
        recipients: List<String>,
        block: SendMessageBuilder.() -> Unit,
    ) {
        for (recipient in recipients) {
            operations.add {
                val result = client.inboxes(inboxId).messages.send {
                    block()
                    to = listOf(recipient)
                }
                sendResults.add(result)
            }
        }
    }

    fun forEachThread(
        inboxId: String,
        filter: ListThreadsBuilder.() -> Unit = {},
        action: suspend (Thread) -> Unit,
    ) {
        operations.add {
            var pageToken: String? = null
            do {
                val finalPageToken = pageToken
                val result = client.inboxes(inboxId).threads.list {
                    filter()
                    finalPageToken?.let { this.pageToken = it }
                }
                for (thread in result.threads) {
                    action(thread)
                }
                pageToken = result.nextPageToken
            } while (pageToken != null)
        }
    }

    internal suspend fun execute(): List<SendMessageResponse> {
        for (op in operations) {
            op()
        }
        return sendResults.toList()
    }
}

suspend fun AgentMail.bulk(block: BulkBuilder.() -> Unit): List<SendMessageResponse> {
    val builder = BulkBuilder(this).apply(block)
    return builder.execute()
}
