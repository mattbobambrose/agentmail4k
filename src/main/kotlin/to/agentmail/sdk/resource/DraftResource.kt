package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import to.agentmail.sdk.builder.*
import to.agentmail.sdk.model.*

class DraftResource internal constructor(
    private val client: HttpClient,
    private val basePath: String,
) {
    suspend fun list(block: ListDraftsBuilder.() -> Unit = {}): DraftList {
        val params = ListDraftsBuilder().apply(block).toQueryParams()
        return client.get(basePath) {
            params.forEach { (k, v) -> parameter(k, v) }
        }.body()
    }

    suspend fun create(block: CreateDraftBuilder.() -> Unit): Draft {
        val body = CreateDraftBuilder().apply(block).build()
        return client.post(basePath) {
            setBody(body)
        }.body()
    }

    suspend fun get(draftId: String): Draft {
        return client.get("$basePath/$draftId").body()
    }

    suspend fun update(draftId: String, block: UpdateDraftBuilder.() -> Unit): Draft {
        val body = UpdateDraftBuilder().apply(block).build()
        return client.patch("$basePath/$draftId") {
            setBody(body)
        }.body()
    }

    suspend fun delete(draftId: String) {
        client.delete("$basePath/$draftId")
    }

    suspend fun send(draftId: String, block: SendDraftBuilder.() -> Unit = {}): SendMessageResponse {
        val body = SendDraftBuilder().apply(block).build()
        return client.post("$basePath/$draftId/send") {
            setBody(body)
        }.body()
    }

    suspend fun getAttachment(draftId: String, attachmentId: String): AttachmentData {
        val response = client.get("$basePath/$draftId/attachments/$attachmentId")
        return AttachmentData(
            data = response.body<ByteArray>(),
            contentType = response.headers["Content-Type"] ?: "application/octet-stream",
        )
    }
}
