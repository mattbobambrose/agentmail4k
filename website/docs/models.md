# Models

All models use `kotlinx.serialization` and `kotlinx.datetime.Instant` for timestamps.

## Core Models

### Inbox

```kotlin
data class Inbox(
    val podId: String?,
    val inboxId: String,
    val email: String,
    val displayName: String?,
    val clientId: String?,
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

### Message

```kotlin
data class Message(
    val inboxId: String,
    val threadId: String,
    val messageId: String,
    val labels: List<String>,
    val timestamp: Instant,
    val from: String,
    val to: List<String>,
    val cc: List<String>,
    val bcc: List<String>,
    val subject: String?,
    val preview: String?,
    val text: String?,
    val html: String?,
    val attachments: List<Attachment>,
    val inReplyTo: String?,
    val references: List<String>,
    val headers: Map<String, String>,
    val size: Int,
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

### Thread

```kotlin
data class Thread(
    val inboxId: String,
    val threadId: String,
    val labels: List<String>,
    val timestamp: Instant,
    val receivedTimestamp: Instant?,
    val sentTimestamp: Instant?,
    val senders: List<String>,
    val recipients: List<String>,
    val subject: String?,
    val preview: String?,
    val attachments: List<Attachment>,
    val lastMessageId: String,
    val messageCount: Int,
    val size: Int,
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

### Draft

```kotlin
data class Draft(
    val inboxId: String,
    val draftId: String,
    val labels: List<String>,
    val timestamp: Instant,
    val from: String?,
    val to: List<String>,
    val cc: List<String>,
    val bcc: List<String>,
    val subject: String?,
    val preview: String?,
    val text: String?,
    val html: String?,
    val attachments: List<Attachment>,
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

### Domain

```kotlin
data class Domain(
    val domainId: String,
    val name: String,
    val verified: Boolean,
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

### Pod

```kotlin
data class Pod(
    val podId: String,
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

### Webhook

```kotlin
data class Webhook(
    val webhookId: String,
    val url: String,
    val events: List<String>,
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

### Attachment

```kotlin
data class Attachment(
    val attachmentId: String,
    val filename: String?,
    val size: Int,
    val contentType: String?,
    val contentDisposition: ContentDisposition?,
    val contentId: String?,
)
```

### AttachmentData

Returned when downloading an attachment:

```kotlin
data class AttachmentData(
    val data: ByteArray,
    val contentType: String,
)
```

### Organization

```kotlin
data class Organization(
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

### ApiKey

```kotlin
data class ApiKey(
    val apiKeyId: String,
    val name: String?,
    val updatedAt: Instant,
    val createdAt: Instant,
)
```

## Response Models

### SendMessageResponse

```kotlin
data class SendMessageResponse(
    val messageId: String,
    val threadId: String,
)
```

### CreateApiKeyResponse

```kotlin
data class CreateApiKeyResponse(
    val apiKeyId: String,
    val apiKey: String,  // only returned at creation
)
```

### RawMessageResponse

```kotlin
data class RawMessageResponse(
    val raw: String,  // RFC 2822 MIME content
)
```

### QueryMetricsResponse

```kotlin
data class QueryMetricsResponse(
    val metrics: List<Metric>,
)

data class Metric(
    val eventType: String,
    val count: Int,
    val timestamp: Instant,
)
```

## List Response Models

All list endpoints return paginated responses with this structure:

| Field | Type | Description |
|---|---|---|
| `count` | `Int` | Total count |
| `limit` | `Int?` | Page size |
| `nextPageToken` | `String?` | Cursor for next page (`null` = last page) |
| *(items)* | `List<T>` | The items (e.g., `inboxes`, `messages`, `threads`) |

## Enums

### ContentDisposition

```kotlin
enum class ContentDisposition { INLINE, ATTACHMENT }
```

### ListDirection

```kotlin
enum class ListDirection(val value: String) {
    ALLOW("allow"),
    BLOCK("block"),
}
```

### ListType

```kotlin
enum class ListType(val value: String) {
    SENDER("sender"),
    RECIPIENT("recipient"),
    DOMAIN("domain"),
    SUBJECT("subject"),
}
```

### MetricsPeriod

```kotlin
enum class MetricsPeriod(val value: String) {
    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
}
```

### WebhookEvent

```kotlin
enum class WebhookEvent(val value: String) {
    MESSAGE_RECEIVED("message.received"),
    MESSAGE_SENT("message.sent"),
    MESSAGE_DELIVERED("message.delivered"),
    MESSAGE_BOUNCED("message.bounced"),
    MESSAGE_COMPLAINED("message.complained"),
    MESSAGE_REJECTED("message.rejected"),
    DOMAIN_VERIFIED("domain.verified"),
}
```
