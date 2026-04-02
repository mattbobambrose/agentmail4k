# Copy for Cursor / Claude

Copy the block below into Cursor or Claude for complete agentmail4k API knowledge in one shot.

```kotlin
/*
agentmail4k — Kotlin DSL for the AgentMail API.

Setup: Add implementation("com.agentmail4k:agentmail4k:0.1.1") to build.gradle.kts.
       Set AGENTMAIL_API_KEY in environment or pass via DSL config.

All API calls are suspend functions. Import DSL extensions from com.agentmail4k.dsl.

Client:
  AgentMailClient()                               — create with env var AGENTMAIL_API_KEY
  AgentMailClient { apiKey = "..."; baseUrl = "..."; timeout { connect = 10.seconds }; retry { maxRetries = 3 } }
  client.close()                                  — or use client.use { ... }

Inboxes:
  client.createInbox(username, domain, displayName)  → Inbox
  client.listInboxes { limit = 10; pageToken = "..." }  → InboxList
  client.getInbox(inboxId)                        → Inbox
  client.updateInbox(inboxId) { displayName = "..." }  → Inbox
  client.deleteInbox(inboxId)

Messages:
  client.sendMessage { from = inboxId; to = listOf("..."); subject = "..."; text = "..."; html = "..." }  → SendMessageResponse
  client.listMessages(inboxId) { limit = 50; labels = listOf("..."); includeSpam = true }  → MessageList
  client.toFullMessage(message)                   → Message (fetches full content from preview)
  client.replyToMessage(message) { text = "..." }  → SendMessageResponse
  client.replyAllToMessage(message) { text = "..." }  → SendMessageResponse
  client.forwardMessage(message) { to = listOf("...") }  → SendMessageResponse
  client.updateMessage(message) { labels = listOf("...") }  → Message (replaces all labels)
  client.updateMessage(message) { addLabels("read"); removeLabels("unread") }  → Message (incremental)
  client.getAttachment(message, attachmentId)     → AttachmentData
  client.getRawMessage(message)                   → RawMessageResponse

Threads:
  client.listThreads(inboxId) { limit = 50 }      → ThreadList
  client.getThread(inboxId, threadId)              → Thread
  client.deleteThread(inboxId, threadId) { permanent = true }
  client.getThreadAttachment(inboxId, threadId, attachmentId)  → AttachmentData

Drafts:
  client.createDraft(inboxId) { to = listOf("..."); subject = "..."; text = "..." }  → Draft
  client.listDrafts(inboxId)                       → DraftList
  client.getDraft(inboxId, draftId)                → Draft
  client.updateDraft(inboxId, draftId) { subject = "..." }  → Draft
  client.deleteDraft(inboxId, draftId)
  client.sendDraft(inboxId, draftId)               → SendMessageResponse
  client.getDraftAttachment(inboxId, draftId, attachmentId)  → AttachmentData

Domains:
  client.createDomain { name = "example.com" }     → Domain
  client.listDomains()                             → DomainList
  client.getDomain(domainId)                       → Domain
  client.updateDomain(domainId) { name = "..." }   → Domain
  client.deleteDomain(domainId)
  client.verifyDomain(domainId)
  client.getDomainZoneFile(domainId)               → ByteArray

Pods:
  client.createPod()                               → Pod
  client.listPods()                                → PodList
  client.getPod(podId)                             → Pod
  client.deletePod(podId)

Webhooks:
  client.createWebhook { url = "..."; events(WebhookEvent.MESSAGE_RECEIVED) }  → Webhook
  client.listWebhooks()                            → WebhookList
  client.getWebhook(webhookId)                     → Webhook
  client.updateWebhook(webhookId) { url = "..." }  → Webhook
  client.deleteWebhook(webhookId)

Lists (allow/block):
  client.createListEntry(ListDirection.ALLOW, ListType.SENDER) { entry = "..." }  → ListEntry
  client.listEntries(ListDirection.BLOCK, ListType.DOMAIN)  → ListEntryList
  client.getListEntry(direction, type, entry)      → ListEntry
  client.deleteListEntry(direction, type, entry)

Metrics:
  client.queryMetrics { eventTypes = "message.received"; period = MetricsPeriod.DAY }  → QueryMetricsResponse

Organization:
  client.getOrganization()                         → Organization

API Keys:
  client.createApiKey { name = "..." }             → CreateApiKeyResponse
  client.listApiKeys()                             → ApiKeyList
  client.deleteApiKey(apiKeyId)

Scoped access (nested resources under inbox or pod):
  client.inboxes(inboxId).messages.list()          — access messages, threads, drafts, lists, metrics, apiKeys
  client.pods(podId).inboxes.list()                — access inboxes, threads, drafts, domains, lists, metrics, apiKeys

Workflows:
  client.monitor(inboxId) { pollInterval = 5.seconds; onMessage { msg -> ... }; onError { e -> ... } }  → Job
  client.poll(inboxId, interval = 10.seconds) { msg -> ... }  → Job
  client.autoReply(inboxId) { rule({ msg -> msg.subject?.contains("help") == true }) { msg -> text = "..." }; default { msg -> text = "..." } }  → Job
  client.bulk { send(inboxId, recipients) { subject = "..."; text = "..." }; forEachThread(inboxId) { thread -> ... } }  → List<SendMessageResponse>
  webhookHandler { signingSecret = "..."; onMessageReceived { payload -> ... } }  → WebhookHandler

Enums:
  WebhookEvent: MESSAGE_RECEIVED, MESSAGE_SENT, MESSAGE_DELIVERED, MESSAGE_BOUNCED, MESSAGE_COMPLAINED, MESSAGE_REJECTED, DOMAIN_VERIFIED
  ListDirection: ALLOW, BLOCK
  ListType: SENDER, RECIPIENT, DOMAIN, SUBJECT
  MetricsPeriod: HOUR, DAY, WEEK, MONTH
  ContentDisposition: INLINE, ATTACHMENT

Errors: SDK raises on 4xx/5xx. Rate limit: 429 with Retry-After. Retries server errors by default (configurable).
*/

import com.agentmail4k.dsl.*
import com.agentmail4k.sdk.AgentMailClient

suspend fun main() {
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
    println("Sent message: ${response.messageId}")

    // List messages
    val messages = client.listMessages(inbox.inboxId) { limit = 10 }
    messages.messages.forEach { println("  ${it.subject}") }

    // Monitor for new messages
    val job = client.monitor(inbox.inboxId) {
        onMessage { msg -> println("New message: ${msg.subject}") }
    }

    client.close()
}
```
