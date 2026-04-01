@file:Suppress("unused")

package website

import com.agentmail4k.dsl.createInbox
import com.agentmail4k.dsl.deleteInbox
import com.agentmail4k.dsl.getInbox
import com.agentmail4k.dsl.listInboxes
import com.agentmail4k.dsl.updateInbox
import com.agentmail4k.sdk.AgentMailClient

// --8<-- [start:create-inbox]
suspend fun createInboxExample() {
    val client = AgentMailClient()
    val inbox = client.createInbox("support", "example.com", "Support Team")
    println("Created: ${inbox.inboxId} (${inbox.email})")
    client.close()
}
// --8<-- [end:create-inbox]

// --8<-- [start:list-inboxes]
suspend fun listInboxesExample() {
    val client = AgentMailClient()
    val result = client.listInboxes {
        limit = 10
        ascending = true
    }
    for (inbox in result.inboxes) {
        println("${inbox.inboxId}: ${inbox.email}")
    }
    client.close()
}
// --8<-- [end:list-inboxes]

// --8<-- [start:get-inbox]
suspend fun getInboxExample() {
    val client = AgentMailClient()
    val inbox = client.getInbox("inbox-id")
    println("Display name: ${inbox.displayName}")
    println("Email: ${inbox.email}")
    client.close()
}
// --8<-- [end:get-inbox]

// --8<-- [start:update-inbox]
suspend fun updateInboxExample() {
    val client = AgentMailClient()
    val updated = client.updateInbox("inbox-id") {
        displayName = "New Display Name"
    }
    println("Updated: ${updated.displayName}")
    client.close()
}
// --8<-- [end:update-inbox]

// --8<-- [start:delete-inbox]
suspend fun deleteInboxExample() {
    val client = AgentMailClient()
    client.deleteInbox("inbox-id")
    println("Inbox deleted")
    client.close()
}
// --8<-- [end:delete-inbox]

// --8<-- [start:paginate-inboxes]
suspend fun paginateInboxesExample() {
    val client = AgentMailClient()
    var pageToken: String? = null

    do {
        val finalToken = pageToken
        val result = client.listInboxes {
            limit = 25
            finalToken?.let { this.pageToken = it }
        }
        for (inbox in result.inboxes) {
            println(inbox.email)
        }
        pageToken = result.nextPageToken
    } while (pageToken != null)

    client.close()
}
// --8<-- [end:paginate-inboxes]
