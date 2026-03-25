package to.agentmail.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.encodeURLPathPart
import to.agentmail.sdk.builder.DeleteThreadBuilder
import to.agentmail.sdk.builder.ListThreadsBuilder
import to.agentmail.sdk.model.AttachmentData
import to.agentmail.sdk.model.Thread
import to.agentmail.sdk.model.ThreadList

class ThreadResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  suspend fun list(block: ListThreadsBuilder.() -> Unit = {}): ThreadList {
    val params = ListThreadsBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  suspend fun get(threadId: String): Thread {
    require(threadId.isNotEmpty()) { "Thread ID must not be empty." }
    return client.get("$basePath/${threadId.encodeURLPathPart()}").body()
  }

  suspend fun delete(threadId: String, block: DeleteThreadBuilder.() -> Unit = {}) {
    require(threadId.isNotEmpty()) { "Thread ID must not be empty." }
    val params = DeleteThreadBuilder().apply(block).toQueryParams()
    client.delete("$basePath/${threadId.encodeURLPathPart()}") {
      params.forEach { (k, v) -> parameter(k, v) }
    }
  }

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
