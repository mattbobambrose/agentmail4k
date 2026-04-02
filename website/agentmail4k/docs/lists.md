# Allow/Block Lists

Lists let you control which senders, recipients, domains, or subjects are allowed or blocked.

## List Direction and Type

Every list operation requires a `ListDirection` and `ListType`:

- **Direction**: `ALLOW` or `BLOCK`
- **Type**: `SENDER`, `RECIPIENT`, `DOMAIN`, or `SUBJECT`

## Block a Sender

```kotlin
--8<-- "Lists.kt:block-sender"
```

## Allow a Domain

```kotlin
--8<-- "Lists.kt:allow-domain"
```

## List Entries

```kotlin
--8<-- "Lists.kt:list-entries"
```

## Get an Entry

```kotlin
--8<-- "Lists.kt:get-entry"
```

## Delete an Entry

```kotlin
--8<-- "Lists.kt:delete-entry"
```

## Other List Types

Block by subject or recipient:

```kotlin
--8<-- "Lists.kt:list-types"
```
