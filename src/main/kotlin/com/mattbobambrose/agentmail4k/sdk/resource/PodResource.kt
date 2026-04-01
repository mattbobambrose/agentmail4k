package com.mattbobambrose.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import com.mattbobambrose.agentmail4k.sdk.builder.ListPodsBuilder
import com.mattbobambrose.agentmail4k.sdk.model.Pod
import com.mattbobambrose.agentmail4k.sdk.model.PodList

/** Provides operations for managing pods: list, create, get, and delete. */
class PodResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {

  /** Lists pods with optional pagination. */
  suspend fun list(block: ListPodsBuilder.() -> Unit = {}): PodList {
    val params = ListPodsBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  /** Creates a new pod. */
  suspend fun create(): Pod {
    return client.post(basePath).body()
  }

  /** Retrieves a pod by ID. */
  suspend fun get(podId: String): Pod {
    require(podId.isNotEmpty()) { "Pod ID must not be empty." }
    return client.get("$basePath/${podId.encodeURLPathPart()}").body()
  }

  /** Deletes a pod by ID. */
  suspend fun delete(podId: String) {
    require(podId.isNotEmpty()) { "Pod ID must not be empty." }
    client.delete("$basePath/${podId.encodeURLPathPart()}")
  }
}
