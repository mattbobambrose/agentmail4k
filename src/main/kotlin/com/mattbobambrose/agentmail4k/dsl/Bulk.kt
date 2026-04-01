package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl
import com.mattbobambrose.agentmail4k.sdk.builder.ListThreadsBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.SendMessageBuilder
import com.mattbobambrose.agentmail4k.sdk.model.SendMessageResponse
import com.mattbobambrose.agentmail4k.sdk.model.Thread

/** DSL builder for batching multiple send and thread-iteration operations into a single execution. */
@AgentMailDsl
class BulkBuilder internal constructor(private val client: AgentMailClient) {
  private val operations = mutableListOf<suspend () -> Unit>()
  private val sendResults = mutableListOf<SendMessageResponse>()

  /** Queues a send operation that delivers a message to each recipient individually. */
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

  /** Queues an operation that iterates over all threads in the inbox, handling pagination automatically. */
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

/** Executes a batch of send and thread-iteration operations. */
suspend fun AgentMailClient.bulk(block: BulkBuilder.() -> Unit): List<SendMessageResponse> {
  val builder = BulkBuilder(this).apply(block)
  return builder.execute()
}
