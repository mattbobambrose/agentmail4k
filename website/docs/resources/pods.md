# Pods

Pods are isolation containers that group inboxes, domains, and other resources together.

## List Pods

```kotlin
val result = client.pods.list {
    limit = 25
}

for (pod in result.pods) {
    println("Pod ${pod.podId} — created: ${pod.createdAt}")
}
```

## Create Pod

```kotlin
val pod = client.pods.create()
println(pod.podId)
```

Pods are created with no parameters — the API assigns an ID automatically.

## Get Pod

```kotlin
val pod = client.pods.get("pod_123")
```

## Delete Pod

```kotlin
client.pods.delete("pod_123")
```

## Scoped Resources

Use `client.pods("podId")` to access resources within a pod. See [Scoped Access](../scoped-access.md) for details.
