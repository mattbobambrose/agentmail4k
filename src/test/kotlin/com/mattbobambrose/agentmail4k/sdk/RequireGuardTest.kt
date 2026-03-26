package com.mattbobambrose.agentmail4k.sdk

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import com.mattbobambrose.agentmail4k.sdk.internal.ApiPaths
import com.mattbobambrose.agentmail4k.sdk.model.ListDirection
import com.mattbobambrose.agentmail4k.sdk.model.ListType
import com.mattbobambrose.agentmail4k.sdk.resource.ApiKeyResource
import com.mattbobambrose.agentmail4k.sdk.resource.DomainResource
import com.mattbobambrose.agentmail4k.sdk.resource.DraftResource
import com.mattbobambrose.agentmail4k.sdk.resource.InboxResource
import com.mattbobambrose.agentmail4k.sdk.resource.InboxScope
import com.mattbobambrose.agentmail4k.sdk.resource.ListResource
import com.mattbobambrose.agentmail4k.sdk.resource.MessageResource
import com.mattbobambrose.agentmail4k.sdk.resource.PodScope
import com.mattbobambrose.agentmail4k.sdk.resource.ThreadResource
import com.mattbobambrose.agentmail4k.sdk.resource.WebhookResource

class RequireGuardTest : StringSpec() {
  private val client = dummyClient()

  init {

    // --- InboxResource ---

    "InboxResource.get() should throw on empty inboxId" {
      val resource = InboxResource(client, ApiPaths.INBOXES)
      shouldThrow<IllegalArgumentException> {
        resource.get("")
      }.message shouldBe "Inbox ID must not be empty."
    }

    "InboxResource.delete() should throw on empty inboxId" {
      val resource = InboxResource(client, ApiPaths.INBOXES)
      shouldThrow<IllegalArgumentException> {
        resource.delete("")
      }.message shouldBe "Inbox ID must not be empty."
    }

    "InboxResource.update() should throw on empty inboxId" {
      val resource = InboxResource(client, ApiPaths.INBOXES)
      shouldThrow<IllegalArgumentException> {
        resource.update("") { displayName = "test" }
      }.message shouldBe "Inbox ID must not be empty."
    }

    // --- MessageResource ---

    "MessageResource.get() should throw on empty messageId" {
      val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
      shouldThrow<IllegalArgumentException> {
        resource.get("")
      }.message shouldBe "Message ID must not be empty."
    }

    "MessageResource.update() should throw on empty messageId" {
      val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
      shouldThrow<IllegalArgumentException> {
        resource.update("") { labels = listOf("read") }
      }.message shouldBe "Message ID must not be empty."
    }

    "MessageResource.reply() should throw on empty messageId" {
      val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
      shouldThrow<IllegalArgumentException> {
        resource.reply("") { text = "reply" }
      }.message shouldBe "Message ID must not be empty."
    }

    "MessageResource.replyAll() should throw on empty messageId" {
      val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
      shouldThrow<IllegalArgumentException> {
        resource.replyAll("") { text = "reply all" }
      }.message shouldBe "Message ID must not be empty."
    }

    "MessageResource.forward() should throw on empty messageId" {
      val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
      shouldThrow<IllegalArgumentException> {
        resource.forward("") { to = listOf("a@b.com") }
      }.message shouldBe "Message ID must not be empty."
    }

    "MessageResource.getAttachment() should throw on empty messageId" {
      val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
      shouldThrow<IllegalArgumentException> {
        resource.getAttachment("", "att_1")
      }.message shouldBe "Message ID must not be empty."
    }

    "MessageResource.getAttachment() should throw on empty attachmentId" {
      val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
      shouldThrow<IllegalArgumentException> {
        resource.getAttachment("msg_1", "")
      }.message shouldBe "Attachment ID must not be empty."
    }

    "MessageResource.getRaw() should throw on empty messageId" {
      val resource = MessageResource(client, "${ApiPaths.INBOXES}/inbox_1/messages")
      shouldThrow<IllegalArgumentException> {
        resource.getRaw("")
      }.message shouldBe "Message ID must not be empty."
    }

    // --- ThreadResource ---

    "ThreadResource.get() should throw on empty threadId" {
      val resource = ThreadResource(client, ApiPaths.THREADS)
      shouldThrow<IllegalArgumentException> {
        resource.get("")
      }.message shouldBe "Thread ID must not be empty."
    }

    "ThreadResource.delete() should throw on empty threadId" {
      val resource = ThreadResource(client, ApiPaths.THREADS)
      shouldThrow<IllegalArgumentException> {
        resource.delete("")
      }.message shouldBe "Thread ID must not be empty."
    }

    "ThreadResource.getAttachment() should throw on empty threadId" {
      val resource = ThreadResource(client, ApiPaths.THREADS)
      shouldThrow<IllegalArgumentException> {
        resource.getAttachment("", "att_1")
      }.message shouldBe "Thread ID must not be empty."
    }

    "ThreadResource.getAttachment() should throw on empty attachmentId" {
      val resource = ThreadResource(client, ApiPaths.THREADS)
      shouldThrow<IllegalArgumentException> {
        resource.getAttachment("thread_1", "")
      }.message shouldBe "Attachment ID must not be empty."
    }

    // --- DraftResource ---

    "DraftResource.get() should throw on empty draftId" {
      val resource = DraftResource(client, ApiPaths.DRAFTS)
      shouldThrow<IllegalArgumentException> {
        resource.get("")
      }.message shouldBe "Draft ID must not be empty."
    }

    "DraftResource.update() should throw on empty draftId" {
      val resource = DraftResource(client, ApiPaths.DRAFTS)
      shouldThrow<IllegalArgumentException> {
        resource.update("") { subject = "test" }
      }.message shouldBe "Draft ID must not be empty."
    }

    "DraftResource.delete() should throw on empty draftId" {
      val resource = DraftResource(client, ApiPaths.DRAFTS)
      shouldThrow<IllegalArgumentException> {
        resource.delete("")
      }.message shouldBe "Draft ID must not be empty."
    }

    "DraftResource.send() should throw on empty draftId" {
      val resource = DraftResource(client, ApiPaths.DRAFTS)
      shouldThrow<IllegalArgumentException> {
        resource.send("")
      }.message shouldBe "Draft ID must not be empty."
    }

    "DraftResource.getAttachment() should throw on empty draftId" {
      val resource = DraftResource(client, ApiPaths.DRAFTS)
      shouldThrow<IllegalArgumentException> {
        resource.getAttachment("", "att_1")
      }.message shouldBe "Draft ID must not be empty."
    }

    "DraftResource.getAttachment() should throw on empty attachmentId" {
      val resource = DraftResource(client, ApiPaths.DRAFTS)
      shouldThrow<IllegalArgumentException> {
        resource.getAttachment("draft_1", "")
      }.message shouldBe "Attachment ID must not be empty."
    }

    // --- DomainResource ---

    "DomainResource.get() should throw on empty domainId" {
      val resource = DomainResource(client, ApiPaths.DOMAINS)
      shouldThrow<IllegalArgumentException> {
        resource.get("")
      }.message shouldBe "Domain ID must not be empty."
    }

    "DomainResource.update() should throw on empty domainId" {
      val resource = DomainResource(client, ApiPaths.DOMAINS)
      shouldThrow<IllegalArgumentException> {
        resource.update("") { name = "test.com" }
      }.message shouldBe "Domain ID must not be empty."
    }

    "DomainResource.delete() should throw on empty domainId" {
      val resource = DomainResource(client, ApiPaths.DOMAINS)
      shouldThrow<IllegalArgumentException> {
        resource.delete("")
      }.message shouldBe "Domain ID must not be empty."
    }

    "DomainResource.verify() should throw on empty domainId" {
      val resource = DomainResource(client, ApiPaths.DOMAINS)
      shouldThrow<IllegalArgumentException> {
        resource.verify("")
      }.message shouldBe "Domain ID must not be empty."
    }

    "DomainResource.getZoneFile() should throw on empty domainId" {
      val resource = DomainResource(client, ApiPaths.DOMAINS)
      shouldThrow<IllegalArgumentException> {
        resource.getZoneFile("")
      }.message shouldBe "Domain ID must not be empty."
    }

    // --- WebhookResource ---

    "WebhookResource.get() should throw on empty webhookId" {
      val resource = WebhookResource(client, ApiPaths.WEBHOOKS)
      shouldThrow<IllegalArgumentException> {
        resource.get("")
      }.message shouldBe "Webhook ID must not be empty."
    }

    "WebhookResource.update() should throw on empty webhookId" {
      val resource = WebhookResource(client, ApiPaths.WEBHOOKS)
      shouldThrow<IllegalArgumentException> {
        resource.update("") { url = "https://example.com" }
      }.message shouldBe "Webhook ID must not be empty."
    }

    "WebhookResource.delete() should throw on empty webhookId" {
      val resource = WebhookResource(client, ApiPaths.WEBHOOKS)
      shouldThrow<IllegalArgumentException> {
        resource.delete("")
      }.message shouldBe "Webhook ID must not be empty."
    }

    // --- ApiKeyResource ---

    "ApiKeyResource.delete() should throw on empty apiKeyId" {
      val resource = ApiKeyResource(client, ApiPaths.API_KEYS)
      shouldThrow<IllegalArgumentException> {
        resource.delete("")
      }.message shouldBe "API key ID must not be empty."
    }

    // --- ListResource ---

    "ListResource.get() should throw on empty entry" {
      val resource = ListResource(client, ApiPaths.LISTS)
      shouldThrow<IllegalArgumentException> {
        resource.get(ListDirection.ALLOW, ListType.SENDER, "")
      }.message shouldBe "List entry must not be empty."
    }

    "ListResource.delete() should throw on empty entry" {
      val resource = ListResource(client, ApiPaths.LISTS)
      shouldThrow<IllegalArgumentException> {
        resource.delete(ListDirection.BLOCK, ListType.DOMAIN, "")
      }.message shouldBe "List entry must not be empty."
    }

    // --- InboxScope and PodScope empty ID propagation ---

    "InboxScope should throw on empty inboxId via ApiPaths" {
      shouldThrow<IllegalArgumentException> {
        InboxScope(client, "")
      }.message shouldBe "Inbox ID must not be empty."
    }

    "PodScope should throw on empty podId via ApiPaths" {
      shouldThrow<IllegalArgumentException> {
        PodScope(client, "")
      }.message shouldBe "Pod ID must not be empty."
    }
  }
}
