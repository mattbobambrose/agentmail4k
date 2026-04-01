package com.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.encodeURLPathPart
import com.agentmail4k.sdk.builder.CreateListEntryBuilder
import com.agentmail4k.sdk.builder.ListEntriesBuilder
import com.agentmail4k.sdk.model.ListDirection
import com.agentmail4k.sdk.model.ListEntry
import com.agentmail4k.sdk.model.ListEntryList
import com.agentmail4k.sdk.model.ListType

/** Provides operations for managing allow/block list entries for filtering by sender, recipient, domain, or subject. */
class ListResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  /** Lists allow/block entries for a given direction and type. */
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

  /** Creates a new allow/block list entry. */
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

  /** Retrieves a specific list entry. */
  suspend fun get(direction: ListDirection, type: ListType, entry: String): ListEntry {
    require(entry.isNotEmpty()) { "List entry must not be empty." }
    return client.get("$basePath/${direction.value}/${type.value}/${entry.encodeURLPathPart()}").body()
  }

  /** Deletes a specific list entry. */
  suspend fun delete(direction: ListDirection, type: ListType, entry: String) {
    require(entry.isNotEmpty()) { "List entry must not be empty." }
    client.delete("$basePath/${direction.value}/${type.value}/${entry.encodeURLPathPart()}")
  }
}
