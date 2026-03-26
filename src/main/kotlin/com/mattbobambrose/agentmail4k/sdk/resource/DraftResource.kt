package com.mattbobambrose.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.encodeURLPathPart
import com.mattbobambrose.agentmail4k.sdk.builder.CreateDraftBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListDraftsBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.SendDraftBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateDraftBuilder
import com.mattbobambrose.agentmail4k.sdk.model.AttachmentData
import com.mattbobambrose.agentmail4k.sdk.model.Draft
import com.mattbobambrose.agentmail4k.sdk.model.DraftList
import com.mattbobambrose.agentmail4k.sdk.model.SendMessageResponse

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
    require(draftId.isNotEmpty()) { "Draft ID must not be empty." }
    return client.get("$basePath/${draftId.encodeURLPathPart()}").body()
  }

  suspend fun update(draftId: String, block: UpdateDraftBuilder.() -> Unit): Draft {
    require(draftId.isNotEmpty()) { "Draft ID must not be empty." }
    val body = UpdateDraftBuilder().apply(block).build()
    return client.patch("$basePath/${draftId.encodeURLPathPart()}") {
      setBody(body)
    }.body()
  }

  suspend fun delete(draftId: String) {
    require(draftId.isNotEmpty()) { "Draft ID must not be empty." }
    client.delete("$basePath/${draftId.encodeURLPathPart()}")
  }

  suspend fun send(draftId: String, block: SendDraftBuilder.() -> Unit = {}): SendMessageResponse {
    require(draftId.isNotEmpty()) { "Draft ID must not be empty." }
    val body = SendDraftBuilder().apply(block).build()
    return client.post("$basePath/${draftId.encodeURLPathPart()}/send") {
      setBody(body)
    }.body()
  }

  suspend fun getAttachment(draftId: String, attachmentId: String): AttachmentData {
    require(draftId.isNotEmpty()) { "Draft ID must not be empty." }
    require(attachmentId.isNotEmpty()) { "Attachment ID must not be empty." }
    val response =
      client.get("$basePath/${draftId.encodeURLPathPart()}/attachments/${attachmentId.encodeURLPathPart()}")
    return AttachmentData(
      data = response.body<ByteArray>(),
      contentType = response.headers["Content-Type"] ?: "application/octet-stream",
    )
  }
}
