package com.agentmail4k.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Represents an entry in an allow/block list. */
@Serializable
data class ListEntry(
  val entry: String,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

/** Paginated list of list entries. */
@Serializable
data class ListEntryList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val entries: List<ListEntry>,
)

/** Represents a pod-level entry in an allow/block list. */
@Serializable
data class PodListEntry(
  val entry: String,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

/** Paginated list of pod-level list entries. */
@Serializable
data class PodListEntryList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val entries: List<PodListEntry>,
)

/** Direction for list filtering: allow or block. */
enum class ListDirection(val value: String) {
  ALLOW("allow"),
  BLOCK("block"),
}

/** Type of list entry: sender, recipient, domain, or subject. */
enum class ListType(val value: String) {
  SENDER("sender"),
  RECIPIENT("recipient"),
  DOMAIN("domain"),
  SUBJECT("subject"),
}
