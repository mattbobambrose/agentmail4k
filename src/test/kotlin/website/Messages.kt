@file:Suppress("unused")

package website

import com.agentmail4k.dsl.forwardMessage
import com.agentmail4k.dsl.getAttachment
import com.agentmail4k.dsl.getRawMessage
import com.agentmail4k.dsl.listMessages
import com.agentmail4k.dsl.replyAllToMessage
import com.agentmail4k.dsl.replyToMessage
import com.agentmail4k.dsl.sendMessage
import com.agentmail4k.dsl.toFullMessage
import com.agentmail4k.dsl.updateMessage
import com.agentmail4k.sdk.AgentMailClient

// --8<-- [start:send-message]
suspend fun sendMessageExample() {
    val client = AgentMailClient()
    val response = client.sendMessage {
        from = "inbox-id"
        to = listOf("recipient@example.com")
        subject = "Hello!"
        text = "Plain text body"
        html = "<h1>Hello!</h1><p>HTML body</p>"
    }
    println("Message ID: ${response!!.messageId}")
    println("Thread ID: ${response.threadId}")
    client.close()
}
// --8<-- [end:send-message]

// --8<-- [start:send-with-cc-bcc]
suspend fun sendWithCcBccExample() {
    val client = AgentMailClient()
    client.sendMessage {
        from = "inbox-id"
        to = listOf("primary@example.com")
        cc = listOf("copy@example.com")
        bcc = listOf("hidden@example.com")
        subject = "Team Update"
        text = "Here's the latest update."
    }
    client.close()
}
// --8<-- [end:send-with-cc-bcc]

// --8<-- [start:list-messages]
suspend fun listMessagesExample() {
    val client = AgentMailClient()
    val result = client.listMessages("inbox-id") {
        limit = 20
        ascending = false
    }
    for (message in result.messages) {
        println("${message.subject} — from ${message.from}")
    }
    client.close()
}
// --8<-- [end:list-messages]

// --8<-- [start:filter-messages]
suspend fun filterMessagesExample() {
    val client = AgentMailClient()
    val result = client.listMessages("inbox-id") {
        labels = listOf("important")
        includeSpam = false
        includeTrash = false
    }
    for (message in result.messages) {
        println(message.subject)
    }
    client.close()
}
// --8<-- [end:filter-messages]

// --8<-- [start:full-message]
suspend fun fullMessageExample() {
    val client = AgentMailClient()

    // List returns message summaries (no body content)
    val result = client.listMessages("inbox-id")
    val summary = result.messages.first()

    // Fetch the full message with body content
    val full = client.toFullMessage(summary)
    println("Subject: ${full.subject}")
    println("Text: ${full.text}")
    println("HTML: ${full.html}")

    client.close()
}
// --8<-- [end:full-message]

// --8<-- [start:reply]
suspend fun replyExample() {
    val client = AgentMailClient()
    val messages = client.listMessages("inbox-id")
    val message = messages.messages.first()

    client.replyToMessage(message) {
        text = "Thanks for reaching out!"
    }
    client.close()
}
// --8<-- [end:reply]

// --8<-- [start:reply-all]
suspend fun replyAllExample() {
    val client = AgentMailClient()
    val messages = client.listMessages("inbox-id")
    val message = messages.messages.first()

    client.replyAllToMessage(message) {
        text = "Thanks everyone!"
    }
    client.close()
}
// --8<-- [end:reply-all]

// --8<-- [start:forward]
suspend fun forwardExample() {
    val client = AgentMailClient()
    val messages = client.listMessages("inbox-id")
    val message = messages.messages.first()

    client.forwardMessage(message) {
        to = listOf("colleague@example.com")
        text = "FYI — see the forwarded message below."
    }
    client.close()
}
// --8<-- [end:forward]

// --8<-- [start:update-message]
suspend fun updateMessageExample() {
    val client = AgentMailClient()
    val messages = client.listMessages("inbox-id")
    val message = messages.messages.first()

    // Replace all labels
    client.updateMessage(message) {
        labels = listOf("important", "reviewed")
    }
    client.close()
}
// --8<-- [end:update-message]

// --8<-- [start:add-remove-labels]
suspend fun addRemoveLabelsExample() {
    val client = AgentMailClient()
    val messages = client.listMessages("inbox-id")
    val message = messages.messages.first()

    // Mark as read
    client.updateMessage(message) {
        addLabels("read")
        removeLabels("unread")
    }
    client.close()
}
// --8<-- [end:add-remove-labels]

// --8<-- [start:attachment]
suspend fun attachmentExample() {
    val client = AgentMailClient()
    val messages = client.listMessages("inbox-id")
    val message = client.toFullMessage(messages.messages.first())

    for (attachment in message.attachments) {
        val data = client.getAttachment(message, attachment.attachmentId)
        println("${attachment.filename}: ${data.contentType} (${data.data.size} bytes)")
    }
    client.close()
}
// --8<-- [end:attachment]

// --8<-- [start:raw-message]
suspend fun rawMessageExample() {
    val client = AgentMailClient()
    val messages = client.listMessages("inbox-id")
    val message = messages.messages.first()

    val raw = client.getRawMessage(message)
    println(raw.raw) // RFC 822 formatted email
    client.close()
}
// --8<-- [end:raw-message]
