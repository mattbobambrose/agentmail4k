# Organization

Retrieve information about the current organization.

## Get Organization

```kotlin
val org = client.organization.get()
println("Created: ${org.createdAt}")
println("Updated: ${org.updatedAt}")
```

The organization resource is read-only — it returns timestamps for the authenticated organization.
