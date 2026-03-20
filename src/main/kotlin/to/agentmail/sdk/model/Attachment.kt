package to.agentmail.sdk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    @SerialName("attachment_id") val attachmentId: String,
    val filename: String? = null,
    val size: Int,
    @SerialName("content_type") val contentType: String? = null,
    @SerialName("content_disposition") val contentDisposition: ContentDisposition? = null,
    @SerialName("content_id") val contentId: String? = null,
)

@Serializable
enum class ContentDisposition {
    @SerialName("inline") INLINE,
    @SerialName("attachment") ATTACHMENT,
}

data class AttachmentData(
    val data: ByteArray,
    val contentType: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttachmentData) return false
        return data.contentEquals(other.data) && contentType == other.contentType
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + contentType.hashCode()
        return result
    }
}
