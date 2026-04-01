package com.mattbobambrose.agentmail4k.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Represents a custom email domain with its verification status and timestamps. */
@Serializable
data class Domain(
  @SerialName("domain_id") val domainId: String,
  val name: String,
  val verified: Boolean,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

/** Paginated list of domains. */
@Serializable
data class DomainList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val domains: List<Domain>,
)
