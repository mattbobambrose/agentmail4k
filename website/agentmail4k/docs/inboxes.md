# Inboxes

Inboxes are email addresses managed by AgentMail. Each inbox can send and receive messages, manage threads, and store drafts.

## Create an Inbox

```kotlin
--8<-- "Inboxes.kt:create-inbox"
```

## List Inboxes

```kotlin
--8<-- "Inboxes.kt:list-inboxes"
```

## Get an Inbox

```kotlin
--8<-- "Inboxes.kt:get-inbox"
```

## Update an Inbox

```kotlin
--8<-- "Inboxes.kt:update-inbox"
```

## Delete an Inbox

```kotlin
--8<-- "Inboxes.kt:delete-inbox"
```

## Pagination

All list operations support pagination with `pageToken`:

```kotlin
--8<-- "Inboxes.kt:paginate-inboxes"
```

The same pagination pattern works for all list operations in the SDK (messages, threads, drafts, etc.).

## Next Steps

- [Messages](messages.md) — send, reply, and forward messages
- [Threads](threads.md) — list and manage conversation threads
- [Drafts](drafts.md) — create, update, and send drafts
