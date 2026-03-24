package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.encodeURLPathPart
import to.agentmail.sdk.builder.CreateListEntryBuilder
import to.agentmail.sdk.builder.ListEntriesBuilder
import to.agentmail.sdk.model.*

class ListResource internal constructor(
    private val client: HttpClient,
    private val basePath: String,
) {
    suspend fun list(
        direction: ListDirection,
        type: ListType,
        block: ListEntriesBuilder.() -> Unit = {},
    ): ListEntryList {
        val params = ListEntriesBuilder().apply(block).toQueryParams()
        return client.get("$basePath/${direction.value}/${type.value}") {
            params.forEach { (k, v) -> parameter(k, v) }
        }.body()
    }

    suspend fun create(
        direction: ListDirection,
        type: ListType,
        block: CreateListEntryBuilder.() -> Unit,
    ): ListEntry {
        val body = CreateListEntryBuilder().apply(block).build()
        return client.post("$basePath/${direction.value}/${type.value}") {
            setBody(body)
        }.body()
    }

    suspend fun get(direction: ListDirection, type: ListType, entry: String): ListEntry {
        return client.get("$basePath/${direction.value}/${type.value}/${entry.encodeURLPathPart()}").body()
    }

    suspend fun delete(direction: ListDirection, type: ListType, entry: String) {
        client.delete("$basePath/${direction.value}/${type.value}/${entry.encodeURLPathPart()}")
    }
}
