package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import to.agentmail.sdk.builder.*
import to.agentmail.sdk.model.*

class MessageResource internal constructor(
    private val client: HttpClient,
    private val basePath: String,
) {
    suspend fun list(block: ListMessagesBuilder.() -> Unit = {}): MessageList {
        val params = ListMessagesBuilder().apply(block).toQueryParams()
        return client.get(basePath) {
            params.forEach { (k, v) -> parameter(k, v) }
        }.body()
    }

    suspend fun get(messageId: String): Message {
        return client.get("$basePath/$messageId").body()
    }

    suspend fun update(messageId: String, block: UpdateMessageBuilder.() -> Unit): Message {
        val body = UpdateMessageBuilder().apply(block).build()
        return client.patch("$basePath/$messageId") {
            setBody(body)
        }.body()
    }

    suspend fun send(block: SendMessageBuilder.() -> Unit): SendMessageResponse {
        val body = SendMessageBuilder().apply(block).build()
        return client.post("$basePath/send") {
            setBody(body)
        }.body()
    }

    suspend fun reply(messageId: String, block: ReplyBuilder.() -> Unit): SendMessageResponse {
        val body = ReplyBuilder().apply(block).build()
        return client.post("$basePath/$messageId/reply") {
            setBody(body)
        }.body()
    }

    suspend fun replyAll(messageId: String, block: ReplyAllBuilder.() -> Unit): SendMessageResponse {
        val body = ReplyAllBuilder().apply(block).build()
        return client.post("$basePath/$messageId/reply-all") {
            setBody(body)
        }.body()
    }

    suspend fun forward(messageId: String, block: ForwardMessageBuilder.() -> Unit): SendMessageResponse {
        val body = ForwardMessageBuilder().apply(block).build()
        return client.post("$basePath/$messageId/forward") {
            setBody(body)
        }.body()
    }

    suspend fun getAttachment(messageId: String, attachmentId: String): AttachmentData {
        val response = client.get("$basePath/$messageId/attachments/$attachmentId")
        return AttachmentData(
            data = response.body<ByteArray>(),
            contentType = response.headers["Content-Type"] ?: "application/octet-stream",
        )
    }

    suspend fun getRaw(messageId: String): RawMessageResponse {
        return client.get("$basePath/$messageId/raw").body()
    }
}
