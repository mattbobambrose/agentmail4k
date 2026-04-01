@file:Suppress("unused")

package website

import com.mattbobambrose.agentmail4k.dsl.createDraft
import com.mattbobambrose.agentmail4k.dsl.deleteDraft
import com.mattbobambrose.agentmail4k.dsl.getDraft
import com.mattbobambrose.agentmail4k.dsl.getDraftAttachment
import com.mattbobambrose.agentmail4k.dsl.listDrafts
import com.mattbobambrose.agentmail4k.dsl.sendDraft
import com.mattbobambrose.agentmail4k.dsl.updateDraft
import com.mattbobambrose.agentmail4k.sdk.AgentMailClient

// --8<-- [start:create-draft]
suspend fun createDraftExample() {
    val client = AgentMailClient()
    val draft = client.createDraft("inbox-id") {
        to = listOf("recipient@example.com")
        subject = "Draft message"
        text = "This is a draft."
    }
    println("Draft ID: ${draft.draftId}")
    client.close()
}
// --8<-- [end:create-draft]

// --8<-- [start:list-drafts]
suspend fun listDraftsExample() {
    val client = AgentMailClient()
    val result = client.listDrafts("inbox-id") {
        limit = 10
    }
    for (draft in result.drafts) {
        println("${draft.draftId}: ${draft.subject}")
    }
    client.close()
}
// --8<-- [end:list-drafts]

// --8<-- [start:get-draft]
suspend fun getDraftExample() {
    val client = AgentMailClient()
    val draft = client.getDraft("inbox-id", "draft-id")
    println("Subject: ${draft.subject}")
    println("To: ${draft.to}")
    client.close()
}
// --8<-- [end:get-draft]

// --8<-- [start:update-draft]
suspend fun updateDraftExample() {
    val client = AgentMailClient()
    val updated = client.updateDraft("inbox-id", "draft-id") {
        subject = "Updated subject"
        text = "Updated body content."
    }
    println("Updated: ${updated.subject}")
    client.close()
}
// --8<-- [end:update-draft]

// --8<-- [start:send-draft]
suspend fun sendDraftExample() {
    val client = AgentMailClient()

    // Create a draft, then send it
    val draft = client.createDraft("inbox-id") {
        to = listOf("recipient@example.com")
        subject = "Ready to send"
        text = "This draft is ready."
    }

    val response = client.sendDraft("inbox-id", draft.draftId)
    println("Sent! Message ID: ${response.messageId}")
    client.close()
}
// --8<-- [end:send-draft]

// --8<-- [start:delete-draft]
suspend fun deleteDraftExample() {
    val client = AgentMailClient()
    client.deleteDraft("inbox-id", "draft-id")
    println("Draft deleted")
    client.close()
}
// --8<-- [end:delete-draft]

// --8<-- [start:draft-attachment]
suspend fun draftAttachmentExample() {
    val client = AgentMailClient()
    val data = client.getDraftAttachment("inbox-id", "draft-id", "attachment-id")
    println("Content type: ${data.contentType}")
    println("Size: ${data.data.size} bytes")
    client.close()
}
// --8<-- [end:draft-attachment]
