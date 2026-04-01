# Scoped Access

The SDK provides two ways to interact with resources: top-level DSL functions and scoped access. Scoped access is useful when performing multiple operations on the same inbox or pod.

## Inbox Scope

Use `client.inboxes(inboxId)` to get a scoped view of an inbox's resources:

```kotlin
--8<-- "ScopedAccess.kt:inbox-scope"
```

### Available Resources

```kotlin
--8<-- "ScopedAccess.kt:inbox-scope-resources"
```

## Pod Scope

Use `client.pods(podId)` to get a scoped view of a pod's resources:

```kotlin
--8<-- "ScopedAccess.kt:pod-scope"
```

### Available Resources

```kotlin
--8<-- "ScopedAccess.kt:pod-scope-resources"
```

## DSL vs Scoped Access

Both approaches access the same API. Choose based on your use case:

```kotlin
--8<-- "ScopedAccess.kt:dsl-vs-scope"
```

**Use DSL functions** when performing a single operation — they're concise and read naturally.

**Use scoped access** when performing multiple operations on the same inbox or pod — you avoid passing the ID repeatedly and make the grouping explicit.
