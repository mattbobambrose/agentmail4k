package com.agentmail4k.sdk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Metadata for an email attachment including filename, size, content type, and disposition. */
@Serializable
data class Attachment(
  @SerialName("attachment_id") val attachmentId: String,
  val filename: String? = null,
  val size: Int,
  @SerialName("content_type") val contentType: String? = null,
  @SerialName("content_disposition") val contentDisposition: ContentDisposition? = null,
  @SerialName("content_id") val contentId: String? = null,
)

/** Disposition type for email attachments: inline or attachment. */
@Serializable
enum class ContentDisposition {
  @SerialName("inline")
  INLINE,
  @SerialName("attachment")
  ATTACHMENT,
}

/** Raw attachment binary data with its content type. */
data class AttachmentData(
  val data: ByteArray,
  val contentType: String,
) {
  /** Compares attachment data by content bytes and content type. */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AttachmentData) return false
    return data.contentEquals(other.data) && contentType == other.contentType
  }

  /** Computes hash from content bytes and content type. */
  override fun hashCode(): Int {
    var result = data.contentHashCode()
    result = 31 * result + contentType.hashCode()
    return result
  }
}
