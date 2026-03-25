# CLAUDE.md

## Project

AgentMail Kotlin SDK — idiomatic DSL-based client for the AgentMail API, built on Ktor and kotlinx.serialization.

## Build & Test

```bash
./gradlew test              # Run all tests
./gradlew compileKotlin     # Compile main sources only
./gradlew run               # Run Main.kt (requires AGENTMAIL_API_KEY env var)
```

## Testing

- Use **Kotest** with **StringSpec** style and `init {}` block
- Test dependencies: `kotest-runner-junit5`, `kotest-assertions-core`, `ktor-client-mock`

```kotlin
class ExampleTest : StringSpec() {
    init {
        "should do something" {
            // test body
        }
    }
}
```

## Architecture

- `AgentMail` — entry point, holds all top-level resources; created via `operator fun invoke` in companion object
- `resource/` — API resource classes (InboxResource, MessageResource, etc.) each take `(HttpClient, basePath)`
- `builder/` — DSL builders for create/update/list/send operations
- `model/` — serializable data classes for API responses
- `workflow/` — high-level utilities (monitor, autoReply, bulk, poll, webhookHandler)
- `internal/ApiPaths` — centralized API path constants; all `"v0/..."` strings live here
- `internal/HttpClientFactory` — Ktor client configuration (auth, timeouts, retries, JSON)

## Key Patterns

- All resource IDs in URL paths are encoded with `encodeURLPathPart()`
- Every method that takes an ID parameter must `require(id.isNotEmpty())` before use
  - Error messages use the format: `"<Entity> ID must not be empty."`
  - `ApiPaths.inbox()` and `ApiPaths.pod()` also validate for early failure at scope creation
- Scoped access: `client.inboxes("id")` and `client.pods("id")` return scope objects for nested resources
- All API calls are `suspend` functions
- `Main.kt` is gitignored — local test harness only
- Dependencies are managed via `gradle/libs.versions.toml` version catalog
- Kotlin context parameters enabled (`-Xcontext-parameters`)

## PR Workflow

- Separate PRs for logically distinct changes
- Squash merge into master
- Run `/simplify` before creating PRs
