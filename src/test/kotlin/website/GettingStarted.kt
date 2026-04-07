@file:Suppress("unused")

package website

import com.agentmail4k.dsl.createInbox
import com.agentmail4k.dsl.sendMessage
import com.agentmail4k.sdk.AgentMailClient

// --8<-- [start:create-client-env]
suspend fun createClientFromEnv() {
    // Reads AGENTMAIL_API_KEY from the environment automatically
    val client = AgentMailClient()
    // ... use the client
    client.close()
}
// --8<-- [end:create-client-env]

// --8<-- [start:create-client-explicit]
suspend fun createClientExplicit() {
    val client = AgentMailClient {
        apiKey = "your-api-key"
    }
    // ... use the client
    client.close()
}
// --8<-- [end:create-client-explicit]

// --8<-- [start:quick-start]
suspend fun quickStart() {
    val client = AgentMailClient()

    // Create an inbox
    val inbox = client.createInbox("support", "example.com", "Support Team")
    println("Created inbox: ${inbox.email}")

    // Send a message
    val response = client.sendMessage {
        from = inbox.inboxId
        to = listOf("user@example.com")
        subject = "Hello from AgentMail!"
        text = "This is a test message sent with agentmail4k."
    }
    println("Sent message: ${response!!.messageId}")

    client.close()
}
// --8<-- [end:quick-start]

// --8<-- [start:use-closeable]
suspend fun useCloseable() {
    AgentMailClient().use { client ->
        val inbox = client.createInbox("hello", "example.com", "Hello")
        println("Inbox: ${inbox.email}")
    } // client is automatically closed
}
// --8<-- [end:use-closeable]
