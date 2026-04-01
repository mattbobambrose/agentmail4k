@file:Suppress("unused")

package website

import com.agentmail4k.dsl.deleteThread
import com.agentmail4k.dsl.getThread
import com.agentmail4k.dsl.getThreadAttachment
import com.agentmail4k.dsl.listThreads
import com.agentmail4k.sdk.AgentMailClient

// --8<-- [start:list-threads]
suspend fun listThreadsExample() {
    val client = AgentMailClient()
    val result = client.listThreads("inbox-id") {
        limit = 10
    }
    for (thread in result.threads) {
        println("${thread.threadId}: ${thread.subject} (${thread.messageCount} messages)")
    }
    client.close()
}
// --8<-- [end:list-threads]

// --8<-- [start:get-thread]
suspend fun getThreadExample() {
    val client = AgentMailClient()
    val thread = client.getThread("inbox-id", "thread-id")
    println("Subject: ${thread.subject}")
    println("Messages: ${thread.messageCount}")
    println("Senders: ${thread.senders}")
    client.close()
}
// --8<-- [end:get-thread]

// --8<-- [start:delete-thread]
suspend fun deleteThreadExample() {
    val client = AgentMailClient()
    client.deleteThread("inbox-id", "thread-id")
    println("Thread deleted")
    client.close()
}
// --8<-- [end:delete-thread]

// --8<-- [start:thread-attachment]
suspend fun threadAttachmentExample() {
    val client = AgentMailClient()
    val data = client.getThreadAttachment("inbox-id", "thread-id", "attachment-id")
    println("Content type: ${data.contentType}")
    println("Size: ${data.data.size} bytes")
    client.close()
}
// --8<-- [end:thread-attachment]
