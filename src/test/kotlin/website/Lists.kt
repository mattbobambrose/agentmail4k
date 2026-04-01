@file:Suppress("unused")

package website

import com.agentmail4k.dsl.createListEntry
import com.agentmail4k.dsl.deleteListEntry
import com.agentmail4k.dsl.getListEntry
import com.agentmail4k.dsl.listEntries
import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.model.ListDirection
import com.agentmail4k.sdk.model.ListType

// --8<-- [start:block-sender]
suspend fun blockSenderExample() {
    val client = AgentMailClient()
    val entry = client.createListEntry(ListDirection.BLOCK, ListType.SENDER) {
        this.entry = "spammer@example.com"
    }
    println("Blocked: ${entry.entry}")
    client.close()
}
// --8<-- [end:block-sender]

// --8<-- [start:allow-domain]
suspend fun allowDomainExample() {
    val client = AgentMailClient()
    val entry = client.createListEntry(ListDirection.ALLOW, ListType.DOMAIN) {
        this.entry = "trusted.com"
    }
    println("Allowed domain: ${entry.entry}")
    client.close()
}
// --8<-- [end:allow-domain]

// --8<-- [start:list-entries]
suspend fun listEntriesExample() {
    val client = AgentMailClient()

    // List all blocked senders
    val blocked = client.listEntries(ListDirection.BLOCK, ListType.SENDER) {
        limit = 25
    }
    for (entry in blocked.entries) {
        println("Blocked: ${entry.entry}")
    }

    // List all allowed domains
    val allowed = client.listEntries(ListDirection.ALLOW, ListType.DOMAIN)
    for (entry in allowed.entries) {
        println("Allowed: ${entry.entry}")
    }

    client.close()
}
// --8<-- [end:list-entries]

// --8<-- [start:get-entry]
suspend fun getEntryExample() {
    val client = AgentMailClient()
    val entry = client.getListEntry(ListDirection.BLOCK, ListType.SENDER, "spammer@example.com")
    println("Entry: ${entry.entry}")
    client.close()
}
// --8<-- [end:get-entry]

// --8<-- [start:delete-entry]
suspend fun deleteEntryExample() {
    val client = AgentMailClient()
    client.deleteListEntry(ListDirection.BLOCK, ListType.SENDER, "spammer@example.com")
    println("Entry removed from block list")
    client.close()
}
// --8<-- [end:delete-entry]

// --8<-- [start:list-types]
suspend fun listTypesExample() {
    val client = AgentMailClient()

    // Block by subject pattern
    client.createListEntry(ListDirection.BLOCK, ListType.SUBJECT) {
        entry = "Buy now"
    }

    // Block by recipient
    client.createListEntry(ListDirection.BLOCK, ListType.RECIPIENT) {
        entry = "no-reply@example.com"
    }

    client.close()
}
// --8<-- [end:list-types]
