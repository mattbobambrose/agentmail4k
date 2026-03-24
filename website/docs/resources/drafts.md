# Drafts

Drafts are unsent messages that can be composed, edited, and sent later.

## List Drafts

```kotlin
val result = client.inboxes("inbox_abc").drafts.list {
    limit = 25
}

for (draft in result.drafts) {
    println("Draft to ${draft.to}: ${draft.subject}")
}
```

## Create Draft

```kotlin
val draft = client.inboxes("inbox_abc").drafts.create {
    to = listOf("recipient@example.com")
    subject = "Draft subject"
    text = "Draft body"
    html = "<p>Draft body</p>"
}

println(draft.draftId)
```

## Get Draft

```kotlin
val draft = client.inboxes("inbox_abc").drafts.get("draft_123")
```

## Update Draft

```kotlin
val updated = client.inboxes("inbox_abc").drafts.update("draft_123") {
    subject = "Updated subject"
    text = "Updated body"
}
```

## Delete Draft

```kotlin
client.inboxes("inbox_abc").drafts.delete("draft_123")
```

## Send Draft

```kotlin
val response = client.inboxes("inbox_abc").drafts.send("draft_123") {
    // Optional overrides at send time
}

println(response.messageId)
println(response.threadId)
```

## Get Draft Attachment

```kotlin
val attachment = client.inboxes("inbox_abc").drafts.getAttachment("draft_123", "att_456")
```
