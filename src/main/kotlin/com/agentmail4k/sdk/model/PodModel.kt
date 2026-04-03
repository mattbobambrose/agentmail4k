package com.agentmail4k.sdk.model

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Represents a pod (isolated workspace) with its ID and timestamps. */
@Serializable
data class Pod(
  @SerialName("pod_id") val podId: String,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

/** Paginated list of pods. */
@Serializable
data class PodList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val pods: List<Pod>,
)
