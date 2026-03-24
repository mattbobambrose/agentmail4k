# Scoped Access

The SDK provides scoped access patterns that let you navigate nested resources naturally. Instead of passing parent IDs to every call, you enter a scope and access child resources directly.

## Inbox Scope

Use `client.inboxes("inboxId")` to access resources within a specific inbox:

```kotlin
val inbox = client.inboxes("inbox_abc")

// Messages
val messages = inbox.messages.list { limit = 50 }
inbox.messages.send { to = listOf("user@example.com"); subject = "Hi" }

// Threads
val threads = inbox.threads.list { ascending = true }

// Drafts
val drafts = inbox.drafts.list()

// Lists (allow/block)
val blocked = inbox.lists.list(ListDirection.BLOCK, ListType.SENDER)

// Metrics
val metrics = inbox.metrics.query { period = MetricsPeriod.DAY }

// API Keys
val keys = inbox.apiKeys.list()
```

### Available Resources in Inbox Scope

| Property | Type | Description |
|---|---|---|
| `messages` | `MessageResource` | Send, receive, reply, forward messages |
| `threads` | `ThreadResource` | List and manage conversation threads |
| `drafts` | `DraftResource` | Create and manage draft messages |
| `lists` | `ListResource` | Allow/block list management |
| `metrics` | `MetricsResource` | Query email metrics |
| `apiKeys` | `ApiKeyResource` | Manage scoped API keys |

## Pod Scope

Use `client.pods("podId")` to access resources within a specific pod:

```kotlin
val pod = client.pods("pod_123")

// Inboxes within this pod
val inboxes = pod.inboxes.list()
pod.inboxes.create { username = "test" }

// Threads across all inboxes in this pod
val threads = pod.threads.list()

// Drafts across all inboxes in this pod
val drafts = pod.drafts.list()

// Domains attached to this pod
val domains = pod.domains.list()

// Lists, metrics, API keys
val lists = pod.lists.list(ListDirection.ALLOW, ListType.DOMAIN)
val metrics = pod.metrics.query()
val keys = pod.apiKeys.list()
```

### Nested Scopes

Pod scopes can nest into inbox scopes:

```kotlin
val pod = client.pods("pod_123")
val inbox = pod.inboxes("inbox_abc")

// Now access messages within a specific inbox in a specific pod
val messages = inbox.messages.list()
```

### Available Resources in Pod Scope

| Property | Type | Description |
|---|---|---|
| `inboxes` | `InboxResource` | Manage inboxes within the pod |
| `threads` | `ThreadResource` | Threads across pod inboxes |
| `drafts` | `DraftResource` | Drafts across pod inboxes |
| `domains` | `DomainResource` | Domain management for the pod |
| `lists` | `ListResource` | Allow/block list management |
| `metrics` | `MetricsResource` | Query pod-level metrics |
| `apiKeys` | `ApiKeyResource` | Manage pod-scoped API keys |

## Top-Level vs Scoped

Many resources are available both at the top level and within a scope:

```kotlin
// Organization-wide threads
val allThreads = client.threads.list()

// Inbox-specific threads
val inboxThreads = client.inboxes("inbox_abc").threads.list()

// Pod-specific threads
val podThreads = client.pods("pod_123").threads.list()
```

The top-level access returns resources across the entire organization. Scoped access filters to the specific inbox or pod.
