# Domains

Domains let you send and receive email on your own domain names.

## List Domains

```kotlin
val result = client.domains.list {
    limit = 25
}

for (domain in result.domains) {
    println("${domain.name} — verified: ${domain.verified}")
}
```

## Create Domain

```kotlin
val domain = client.domains.create {
    name = "example.com"
}

println(domain.domainId)
```

## Get Domain

```kotlin
val domain = client.domains.get("domain_123")
```

## Update Domain

```kotlin
val updated = client.domains.update("domain_123") {
    name = "newname.com"
}
```

## Delete Domain

```kotlin
client.domains.delete("domain_123")
```

## Verify Domain

Trigger DNS verification for a domain:

```kotlin
client.domains.verify("domain_123")
```

## Get Zone File

Download the DNS zone file for configuring your domain:

```kotlin
val zoneFile: ByteArray = client.domains.getZoneFile("domain_123")
println(String(zoneFile))
```
