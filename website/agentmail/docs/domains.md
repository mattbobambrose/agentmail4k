# Domains

Custom domains allow you to send and receive email from your own domain.

## Create a Domain

```kotlin
--8<-- "Domains.kt:create-domain"
```

## List Domains

```kotlin
--8<-- "Domains.kt:list-domains"
```

## Get a Domain

```kotlin
--8<-- "Domains.kt:get-domain"
```

## Update a Domain

```kotlin
--8<-- "Domains.kt:update-domain"
```

## Verify a Domain

After creating a domain, you need to add DNS records and verify ownership:

```kotlin
--8<-- "Domains.kt:verify-domain"
```

## Get Zone File

Retrieve the DNS zone file for a domain:

```kotlin
--8<-- "Domains.kt:zone-file"
```

## Delete a Domain

```kotlin
--8<-- "Domains.kt:delete-domain"
```
