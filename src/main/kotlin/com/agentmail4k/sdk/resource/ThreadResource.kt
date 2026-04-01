package com.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.encodeURLPathPart
import com.agentmail4k.sdk.builder.DeleteThreadBuilder
import com.agentmail4k.sdk.builder.ListThreadsBuilder
import com.agentmail4k.sdk.model.AttachmentData
import com.agentmail4k.sdk.model.Thread
import com.agentmail4k.sdk.model.ThreadList

/** Provides operations for managing email threads: list, get, delete, and retrieve attachments. */
class ThreadResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  /** Lists threads with optional pagination and filtering. */
  suspend fun list(block: ListThreadsBuilder.() -> Unit = {}): ThreadList {
    val params = ListThreadsBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  /** Retrieves a thread by ID. */
  suspend fun get(threadId: String): Thread {
    require(threadId.isNotEmpty()) { "Thread ID must not be empty." }
    return client.get("$basePath/${threadId.encodeURLPathPart()}").body()
  }

  /** Deletes a thread by ID with optional permanent deletion. */
  suspend fun delete(threadId: String, block: DeleteThreadBuilder.() -> Unit = {}) {
    require(threadId.isNotEmpty()) { "Thread ID must not be empty." }
    val params = DeleteThreadBuilder().apply(block).toQueryParams()
    client.delete("$basePath/${threadId.encodeURLPathPart()}") {
      params.forEach { (k, v) -> parameter(k, v) }
    }
  }

  /** Retrieves a thread attachment's binary data. */
  suspend fun getAttachment(threadId: String, attachmentId: String): AttachmentData {
    require(threadId.isNotEmpty()) { "Thread ID must not be empty." }
    require(attachmentId.isNotEmpty()) { "Attachment ID must not be empty." }
    val response =
      client.get("$basePath/${threadId.encodeURLPathPart()}/attachments/${attachmentId.encodeURLPathPart()}")
    return AttachmentData(
      data = response.body<ByteArray>(),
      contentType = response.headers["Content-Type"] ?: "application/octet-stream",
    )
  }
}
