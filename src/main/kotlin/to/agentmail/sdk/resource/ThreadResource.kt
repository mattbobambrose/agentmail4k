package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
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
        return client.get("$basePath/$threadId").body()
    }

    suspend fun delete(threadId: String, block: DeleteThreadBuilder.() -> Unit = {}) {
        val params = DeleteThreadBuilder().apply(block).toQueryParams()
        client.delete("$basePath/$threadId") {
            params.forEach { (k, v) -> parameter(k, v) }
        }
    }

    suspend fun getAttachment(threadId: String, attachmentId: String): AttachmentData {
        val response = client.get("$basePath/$threadId/attachments/$attachmentId")
        return AttachmentData(
            data = response.body<ByteArray>(),
            contentType = response.headers["Content-Type"] ?: "application/octet-stream",
        )
    }
}
