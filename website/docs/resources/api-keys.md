# API Keys

Manage API keys for authentication. Keys can be scoped to specific inboxes or pods.

## List API Keys

```kotlin
val result = client.apiKeys.list {
    limit = 25
}

for (key in result.apiKeys) {
    println("${key.name ?: "(unnamed)"} — ${key.apiKeyId}")
}
```

## Create API Key

```kotlin
val response = client.apiKeys.create {
    name = "my-service-key"
}

println(response.apiKeyId)
println(response.apiKey)  // Only returned at creation time — save it!
```

!!! warning
    The `apiKey` value is only returned once when the key is created. Store it securely.

## Delete API Key

```kotlin
client.apiKeys.delete("apikey_123")
```

## Scoped Access

API keys are available at multiple levels:

```kotlin
// Organization level
client.apiKeys.list()

// Inbox level
client.inboxes("inbox_abc").apiKeys.create { name = "inbox-key" }

// Pod level
client.pods("pod_123").apiKeys.list()
```
