package com.mattbobambrose.agentmail4k.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListEntry(
  val entry: String,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

@Serializable
data class ListEntryList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val entries: List<ListEntry>,
)

@Serializable
data class PodListEntry(
  val entry: String,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

@Serializable
data class PodListEntryList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val entries: List<PodListEntry>,
)

enum class ListDirection(val value: String) {
  ALLOW("allow"),
  BLOCK("block"),
}

enum class ListType(val value: String) {
  SENDER("sender"),
  RECIPIENT("recipient"),
  DOMAIN("domain"),
  SUBJECT("subject"),
}
