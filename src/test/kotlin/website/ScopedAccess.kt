@file:Suppress("unused")

package website

import com.agentmail4k.dsl.listMessages
import com.agentmail4k.sdk.AgentMailClient

// --8<-- [start:inbox-scope]
suspend fun inboxScopeExample() {
    val client = AgentMailClient()

    // Get a scoped view of an inbox's resources
    val scope = client.inboxes("inbox-id")

    // Access messages, threads, drafts, etc. within this inbox
    val messages = scope.messages.list()
    val threads = scope.threads.list()
    val drafts = scope.drafts.list()

    println("Messages: ${messages.messages.size}")
    println("Threads: ${threads.threads.size}")
    println("Drafts: ${drafts.drafts.size}")

    client.close()
}
// --8<-- [end:inbox-scope]

// --8<-- [start:inbox-scope-resources]
suspend fun inboxScopeResourcesExample() {
    val client = AgentMailClient()
    val scope = client.inboxes("inbox-id")

    // All resources available on an InboxScope:
    scope.messages    // MessageResource — send, list, reply, forward
    scope.threads     // ThreadResource — list, get, delete
    scope.drafts      // DraftResource — create, list, send
    scope.lists       // ListResource — allow/block lists
    scope.metrics     // MetricsResource — query metrics
    scope.apiKeys     // ApiKeyResource — manage API keys

    client.close()
}
// --8<-- [end:inbox-scope-resources]

// --8<-- [start:pod-scope]
suspend fun podScopeExample() {
    val client = AgentMailClient()

    // Get a scoped view of a pod's resources
    val scope = client.pods("pod-id")

    // Access inboxes, threads, etc. within this pod
    val inboxes = scope.inboxes.list()
    val threads = scope.threads.list()

    println("Inboxes in pod: ${inboxes.inboxes.size}")
    println("Threads in pod: ${threads.threads.size}")

    client.close()
}
// --8<-- [end:pod-scope]

// --8<-- [start:pod-scope-resources]
suspend fun podScopeResourcesExample() {
    val client = AgentMailClient()
    val scope = client.pods("pod-id")

    // All resources available on a PodScope:
    scope.inboxes     // InboxResource — manage pod inboxes
    scope.threads     // ThreadResource — list, get, delete
    scope.drafts      // DraftResource — create, list, send
    scope.domains     // DomainResource — manage pod domains
    scope.lists       // ListResource — allow/block lists
    scope.metrics     // MetricsResource — query metrics
    scope.apiKeys     // ApiKeyResource — manage API keys

    client.close()
}
// --8<-- [end:pod-scope-resources]

// --8<-- [start:dsl-vs-scope]
suspend fun dslVsScopeExample() {
    val client = AgentMailClient()

    // DSL approach — convenient for one-off operations
    val messages = client.listMessages("inbox-id") { limit = 10 }

    // Scoped approach — efficient when doing multiple operations on the same inbox
    val scope = client.inboxes("inbox-id")
    val msgs = scope.messages.list { limit = 10 }
    val threads = scope.threads.list { limit = 10 }
    val drafts = scope.drafts.list { limit = 10 }

    client.close()
}
// --8<-- [end:dsl-vs-scope]
