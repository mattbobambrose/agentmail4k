package com.agentmail4k.dsl

import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.testInstant
import com.agentmail4k.sdk.testMessage
import com.agentmail4k.sdk.model.ApiKeyList
import com.agentmail4k.sdk.model.AttachmentData
import com.agentmail4k.sdk.model.CreateApiKeyResponse
import com.agentmail4k.sdk.model.Domain
import com.agentmail4k.sdk.model.DomainList
import com.agentmail4k.sdk.model.Draft
import com.agentmail4k.sdk.model.DraftList
import com.agentmail4k.sdk.model.Inbox
import com.agentmail4k.sdk.model.InboxList
import com.agentmail4k.sdk.model.ListDirection
import com.agentmail4k.sdk.model.ListEntry
import com.agentmail4k.sdk.model.ListEntryList
import com.agentmail4k.sdk.model.ListType
import com.agentmail4k.sdk.model.MessageList
import com.agentmail4k.sdk.model.Organization
import com.agentmail4k.sdk.model.Pod
import com.agentmail4k.sdk.model.PodList
import com.agentmail4k.sdk.model.QueryMetricsResponse
import com.agentmail4k.sdk.model.RawMessageResponse
import com.agentmail4k.sdk.model.SendMessageResponse
import com.agentmail4k.sdk.model.ThreadList
import com.agentmail4k.sdk.model.Webhook
import com.agentmail4k.sdk.model.WebhookList
import com.agentmail4k.sdk.resource.ApiKeyResource
import com.agentmail4k.sdk.resource.DomainResource
import com.agentmail4k.sdk.resource.InboxResource
import com.agentmail4k.sdk.resource.InboxScope
import com.agentmail4k.sdk.resource.ListResource
import com.agentmail4k.sdk.resource.MetricsResource
import com.agentmail4k.sdk.resource.OrganizationResource
import com.agentmail4k.sdk.resource.PodResource
import com.agentmail4k.sdk.resource.WebhookResource
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

private fun mockTopLevelClient(): AgentMailClient = mockk(relaxUnitFun = true)

private fun mockInboxScopeClient(inboxId: String): Pair<AgentMailClient, InboxScope> {
  val mockScope = mockk<InboxScope> {
    every { messages } returns mockk()
    every { threads } returns mockk()
    every { drafts } returns mockk()
    every { lists } returns mockk()
    every { metrics } returns mockk()
    every { apiKeys } returns mockk()
  }
  val client = mockk<AgentMailClient>(relaxUnitFun = true) {
    every { inboxes(inboxId) } returns mockScope
  }
  return client to mockScope
}

class DslWrapperTest : StringSpec({

  // --- Inbox wrappers ---

  "listInboxes delegates to inboxes resource" {
    val client = mockTopLevelClient()
    val mockInboxes = mockk<InboxResource>()
    every { client.inboxes } returns mockInboxes
    coEvery { mockInboxes.list(any()) } returns InboxList(count = 0, inboxes = emptyList())

    client.listInboxes().count shouldBe 0
  }

  "getInbox delegates to inboxes resource" {
    val client = mockTopLevelClient()
    val mockInboxes = mockk<InboxResource>()
    every { client.inboxes } returns mockInboxes
    val inbox = Inbox(inboxId = "inbox_1", email = "a@test.com", updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockInboxes.get("inbox_1") } returns inbox

    client.getInbox("inbox_1").inboxId shouldBe "inbox_1"
  }

  "updateInbox delegates to inboxes resource" {
    val client = mockTopLevelClient()
    val mockInboxes = mockk<InboxResource>()
    every { client.inboxes } returns mockInboxes
    val inbox = Inbox(
      inboxId = "inbox_1", email = "a@test.com", displayName = "Updated",
      updatedAt = testInstant, createdAt = testInstant,
    )
    coEvery { mockInboxes.update(any(), any()) } returns inbox

    client.updateInbox("inbox_1") { displayName = "Updated" }.displayName shouldBe "Updated"
    coVerify { mockInboxes.update("inbox_1", any()) }
  }

  "deleteInbox delegates to inboxes resource" {
    val client = mockTopLevelClient()
    val mockInboxes = mockk<InboxResource>()
    every { client.inboxes } returns mockInboxes
    coEvery { mockInboxes.delete(any()) } returns Unit

    client.deleteInbox("inbox_1")
    coVerify { mockInboxes.delete("inbox_1") }
  }

  // --- Message wrappers ---

  "listMessages delegates to inbox scope messages" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    val mockMessages = scope.messages
    coEvery { mockMessages.list(any()) } returns MessageList(count = 2, messages = emptyList())

    client.listMessages("inbox_1") { limit = 10 }.count shouldBe 2
  }

  "replyToMessage uses message inboxId and messageId" {
    val msg = testMessage(messageId = "msg_1", inboxId = "inbox_1")
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.messages.reply(any(), any()) } returns SendMessageResponse("msg_reply", "thread_1")

    client.replyToMessage(msg) { text = "Got it" }.messageId shouldBe "msg_reply"
    coVerify { scope.messages.reply("msg_1", any()) }
  }

  "replyAllToMessage uses message inboxId and messageId" {
    val msg = testMessage(messageId = "msg_1", inboxId = "inbox_1")
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.messages.replyAll(any(), any()) } returns SendMessageResponse("msg_ra", "thread_1")

    client.replyAllToMessage(msg) { text = "Noted" }.messageId shouldBe "msg_ra"
    coVerify { scope.messages.replyAll("msg_1", any()) }
  }

  "forwardMessage uses message inboxId and messageId" {
    val msg = testMessage(messageId = "msg_1", inboxId = "inbox_1")
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.messages.forward(any(), any()) } returns SendMessageResponse("msg_fwd", "thread_2")

    client.forwardMessage(msg) { to = listOf("other@example.com") }.messageId shouldBe "msg_fwd"
    coVerify { scope.messages.forward("msg_1", any()) }
  }

  "updateMessage uses message inboxId and messageId" {
    val msg = testMessage(messageId = "msg_1", inboxId = "inbox_1")
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.messages.update(any(), any()) } returns msg

    client.updateMessage(msg) { labels = listOf("read") }.messageId shouldBe "msg_1"
    coVerify { scope.messages.update("msg_1", any()) }
  }

  "getAttachment uses message inboxId and messageId" {
    val msg = testMessage(messageId = "msg_1", inboxId = "inbox_1")
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.messages.getAttachment(any(), any()) } returns AttachmentData("pdf".toByteArray(), "application/pdf")

    client.getAttachment(msg, "att_1").contentType shouldBe "application/pdf"
    coVerify { scope.messages.getAttachment("msg_1", "att_1") }
  }

  "getRawMessage uses message inboxId and messageId" {
    val msg = testMessage(messageId = "msg_1", inboxId = "inbox_1")
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.messages.getRaw(any()) } returns RawMessageResponse("MIME-Version: 1.0")

    client.getRawMessage(msg).raw shouldBe "MIME-Version: 1.0"
    coVerify { scope.messages.getRaw("msg_1") }
  }

  // --- Thread wrappers ---

  "listThreads delegates to inbox scope threads" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.threads.list(any()) } returns ThreadList(count = 3, threads = emptyList())

    client.listThreads("inbox_1").count shouldBe 3
  }

  "getThread delegates to inbox scope threads" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.threads.get(any()) } returns mockk { every { threadId } returns "thread_1" }

    client.getThread("inbox_1", "thread_1").threadId shouldBe "thread_1"
    coVerify { scope.threads.get("thread_1") }
  }

  "deleteThread delegates to inbox scope threads" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.threads.delete(any(), any()) } returns Unit

    client.deleteThread("inbox_1", "thread_1") { permanent = true }
    coVerify { scope.threads.delete("thread_1", any()) }
  }

  "getThreadAttachment delegates to inbox scope threads" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.threads.getAttachment(any(), any()) } returns AttachmentData("img".toByteArray(), "image/png")

    client.getThreadAttachment("inbox_1", "thread_1", "att_1").contentType shouldBe "image/png"
    coVerify { scope.threads.getAttachment("thread_1", "att_1") }
  }

  // --- Draft wrappers ---

  "listDrafts delegates to inbox scope drafts" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.drafts.list(any()) } returns DraftList(count = 0, drafts = emptyList())

    client.listDrafts("inbox_1").count shouldBe 0
  }

  "createDraft delegates to inbox scope drafts" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    val draft = Draft(
      inboxId = "inbox_1", draftId = "draft_1", timestamp = testInstant,
      updatedAt = testInstant, createdAt = testInstant,
    )
    coEvery { scope.drafts.create(any()) } returns draft

    client.createDraft("inbox_1") { to = listOf("a@b.com") }.draftId shouldBe "draft_1"
  }

  "getDraft delegates to inbox scope drafts" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    val draft = Draft(
      inboxId = "inbox_1", draftId = "draft_1", timestamp = testInstant,
      updatedAt = testInstant, createdAt = testInstant,
    )
    coEvery { scope.drafts.get(any()) } returns draft

    client.getDraft("inbox_1", "draft_1").draftId shouldBe "draft_1"
    coVerify { scope.drafts.get("draft_1") }
  }

  "updateDraft delegates to inbox scope drafts" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    val draft = Draft(
      inboxId = "inbox_1", draftId = "draft_1", subject = "Updated",
      timestamp = testInstant, updatedAt = testInstant, createdAt = testInstant,
    )
    coEvery { scope.drafts.update(any(), any()) } returns draft

    client.updateDraft("inbox_1", "draft_1") { subject = "Updated" }.subject shouldBe "Updated"
    coVerify { scope.drafts.update("draft_1", any()) }
  }

  "deleteDraft delegates to inbox scope drafts" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.drafts.delete(any()) } returns Unit

    client.deleteDraft("inbox_1", "draft_1")
    coVerify { scope.drafts.delete("draft_1") }
  }

  "sendDraft delegates to inbox scope drafts" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.drafts.send(any(), any()) } returns SendMessageResponse("msg_sent", "thread_1")

    client.sendDraft("inbox_1", "draft_1").messageId shouldBe "msg_sent"
    coVerify { scope.drafts.send("draft_1", any()) }
  }

  "getDraftAttachment delegates to inbox scope drafts" {
    val (client, scope) = mockInboxScopeClient("inbox_1")
    coEvery { scope.drafts.getAttachment(any(), any()) } returns AttachmentData("doc".toByteArray(), "text/plain")

    client.getDraftAttachment("inbox_1", "draft_1", "att_1").contentType shouldBe "text/plain"
    coVerify { scope.drafts.getAttachment("draft_1", "att_1") }
  }

  // --- Domain wrappers ---

  "listDomains delegates to domains resource" {
    val client = mockTopLevelClient()
    val mockDomains = mockk<DomainResource>()
    every { client.domains } returns mockDomains
    coEvery { mockDomains.list(any()) } returns DomainList(count = 1, domains = emptyList())

    client.listDomains().count shouldBe 1
  }

  "createDomain delegates to domains resource" {
    val client = mockTopLevelClient()
    val mockDomains = mockk<DomainResource>()
    every { client.domains } returns mockDomains
    val domain = Domain(domainId = "dom_1", name = "example.com", verified = false, updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockDomains.create(any()) } returns domain

    client.createDomain { name = "example.com" }.domainId shouldBe "dom_1"
  }

  "getDomain delegates to domains resource" {
    val client = mockTopLevelClient()
    val mockDomains = mockk<DomainResource>()
    every { client.domains } returns mockDomains
    val domain = Domain(domainId = "dom_1", name = "example.com", verified = false, updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockDomains.get("dom_1") } returns domain

    client.getDomain("dom_1").name shouldBe "example.com"
  }

  "updateDomain delegates to domains resource" {
    val client = mockTopLevelClient()
    val mockDomains = mockk<DomainResource>()
    every { client.domains } returns mockDomains
    val domain = Domain(domainId = "dom_1", name = "example.com", verified = false, updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockDomains.update(any(), any()) } returns domain

    client.updateDomain("dom_1") { }.domainId shouldBe "dom_1"
    coVerify { mockDomains.update("dom_1", any()) }
  }

  "deleteDomain delegates to domains resource" {
    val client = mockTopLevelClient()
    val mockDomains = mockk<DomainResource>()
    every { client.domains } returns mockDomains
    coEvery { mockDomains.delete(any()) } returns Unit

    client.deleteDomain("dom_1")
    coVerify { mockDomains.delete("dom_1") }
  }

  "verifyDomain delegates to domains resource" {
    val client = mockTopLevelClient()
    val mockDomains = mockk<DomainResource>()
    every { client.domains } returns mockDomains
    coEvery { mockDomains.verify(any()) } returns Unit

    client.verifyDomain("dom_1")
    coVerify { mockDomains.verify("dom_1") }
  }

  "getDomainZoneFile delegates to domains resource" {
    val client = mockTopLevelClient()
    val mockDomains = mockk<DomainResource>()
    every { client.domains } returns mockDomains
    coEvery { mockDomains.getZoneFile("dom_1") } returns "zone-data".toByteArray()

    client.getDomainZoneFile("dom_1").decodeToString() shouldBe "zone-data"
  }

  // --- Pod wrappers ---

  "listPods delegates to pods resource" {
    val client = mockTopLevelClient()
    val mockPods = mockk<PodResource>()
    every { client.pods } returns mockPods
    coEvery { mockPods.list(any()) } returns PodList(count = 2, pods = emptyList())

    client.listPods().count shouldBe 2
  }

  "createPod delegates to pods resource" {
    val client = mockTopLevelClient()
    val mockPods = mockk<PodResource>()
    every { client.pods } returns mockPods
    val pod = Pod(podId = "pod_1", updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockPods.create() } returns pod

    client.createPod().podId shouldBe "pod_1"
  }

  "getPod delegates to pods resource" {
    val client = mockTopLevelClient()
    val mockPods = mockk<PodResource>()
    every { client.pods } returns mockPods
    val pod = Pod(podId = "pod_1", updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockPods.get("pod_1") } returns pod

    client.getPod("pod_1").podId shouldBe "pod_1"
  }

  "deletePod delegates to pods resource" {
    val client = mockTopLevelClient()
    val mockPods = mockk<PodResource>()
    every { client.pods } returns mockPods
    coEvery { mockPods.delete(any()) } returns Unit

    client.deletePod("pod_1")
    coVerify { mockPods.delete("pod_1") }
  }

  // --- Webhook wrappers ---

  "listWebhooks delegates to webhooks resource" {
    val client = mockTopLevelClient()
    val mockWebhooks = mockk<WebhookResource>()
    every { client.webhooks } returns mockWebhooks
    coEvery { mockWebhooks.list(any()) } returns WebhookList(count = 0, webhooks = emptyList())

    client.listWebhooks().count shouldBe 0
  }

  "createWebhook delegates to webhooks resource" {
    val client = mockTopLevelClient()
    val mockWebhooks = mockk<WebhookResource>()
    every { client.webhooks } returns mockWebhooks
    val webhook = Webhook(
      webhookId = "wh_1", url = "https://example.com/hook", events = listOf("message.received"),
      updatedAt = testInstant, createdAt = testInstant,
    )
    coEvery { mockWebhooks.create(any()) } returns webhook

    client.createWebhook { url = "https://example.com/hook" }.webhookId shouldBe "wh_1"
  }

  "getWebhook delegates to webhooks resource" {
    val client = mockTopLevelClient()
    val mockWebhooks = mockk<WebhookResource>()
    every { client.webhooks } returns mockWebhooks
    val webhook = Webhook(
      webhookId = "wh_1", url = "https://example.com/hook", events = emptyList(),
      updatedAt = testInstant, createdAt = testInstant,
    )
    coEvery { mockWebhooks.get("wh_1") } returns webhook

    client.getWebhook("wh_1").url shouldBe "https://example.com/hook"
  }

  "updateWebhook delegates to webhooks resource" {
    val client = mockTopLevelClient()
    val mockWebhooks = mockk<WebhookResource>()
    every { client.webhooks } returns mockWebhooks
    val webhook = Webhook(
      webhookId = "wh_1", url = "https://example.com/hook2", events = emptyList(),
      updatedAt = testInstant, createdAt = testInstant,
    )
    coEvery { mockWebhooks.update(any(), any()) } returns webhook

    client.updateWebhook("wh_1") { url = "https://example.com/hook2" }.url shouldBe "https://example.com/hook2"
    coVerify { mockWebhooks.update("wh_1", any()) }
  }

  "deleteWebhook delegates to webhooks resource" {
    val client = mockTopLevelClient()
    val mockWebhooks = mockk<WebhookResource>()
    every { client.webhooks } returns mockWebhooks
    coEvery { mockWebhooks.delete(any()) } returns Unit

    client.deleteWebhook("wh_1")
    coVerify { mockWebhooks.delete("wh_1") }
  }

  // --- List entry wrappers ---

  "listEntries delegates to lists resource" {
    val client = mockTopLevelClient()
    val mockLists = mockk<ListResource>()
    every { client.lists } returns mockLists
    coEvery { mockLists.list(any(), any(), any()) } returns ListEntryList(count = 0, entries = emptyList())

    client.listEntries(ListDirection.ALLOW, ListType.SENDER).count shouldBe 0
    coVerify { mockLists.list(ListDirection.ALLOW, ListType.SENDER, any()) }
  }

  "createListEntry delegates to lists resource" {
    val client = mockTopLevelClient()
    val mockLists = mockk<ListResource>()
    every { client.lists } returns mockLists
    val entry = ListEntry(entry = "spam@bad.com", updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockLists.create(any(), any(), any()) } returns entry

    client.createListEntry(ListDirection.BLOCK, ListType.SENDER) { this.entry = "spam@bad.com" }.entry shouldBe "spam@bad.com"
  }

  "getListEntry delegates to lists resource" {
    val client = mockTopLevelClient()
    val mockLists = mockk<ListResource>()
    every { client.lists } returns mockLists
    val entry = ListEntry(entry = "spam.com", updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockLists.get(any(), any(), any()) } returns entry

    client.getListEntry(ListDirection.BLOCK, ListType.DOMAIN, "spam.com").entry shouldBe "spam.com"
    coVerify { mockLists.get(ListDirection.BLOCK, ListType.DOMAIN, "spam.com") }
  }

  "deleteListEntry delegates to lists resource" {
    val client = mockTopLevelClient()
    val mockLists = mockk<ListResource>()
    every { client.lists } returns mockLists
    coEvery { mockLists.delete(any(), any(), any()) } returns Unit

    client.deleteListEntry(ListDirection.ALLOW, ListType.SUBJECT, "test")
    coVerify { mockLists.delete(ListDirection.ALLOW, ListType.SUBJECT, "test") }
  }

  // --- Metrics wrapper ---

  "queryMetrics delegates to metrics resource" {
    val client = mockTopLevelClient()
    val mockMetrics = mockk<MetricsResource>()
    every { client.metrics } returns mockMetrics
    coEvery { mockMetrics.query(any()) } returns QueryMetricsResponse(metrics = emptyList())

    client.queryMetrics().metrics shouldBe emptyList()
  }

  // --- Organization wrapper ---

  "getOrganization delegates to organization resource" {
    val client = mockTopLevelClient()
    val mockOrg = mockk<OrganizationResource>()
    every { client.organization } returns mockOrg
    val org = Organization(updatedAt = testInstant, createdAt = testInstant)
    coEvery { mockOrg.get() } returns org

    client.getOrganization().updatedAt shouldBe testInstant
  }

  // --- API Key wrappers ---

  "listApiKeys delegates to apiKeys resource" {
    val client = mockTopLevelClient()
    val mockApiKeys = mockk<ApiKeyResource>()
    every { client.apiKeys } returns mockApiKeys
    coEvery { mockApiKeys.list(any()) } returns ApiKeyList(count = 0, apiKeys = emptyList())

    client.listApiKeys().count shouldBe 0
  }

  "createApiKey delegates to apiKeys resource" {
    val client = mockTopLevelClient()
    val mockApiKeys = mockk<ApiKeyResource>()
    every { client.apiKeys } returns mockApiKeys
    coEvery { mockApiKeys.create(any()) } returns CreateApiKeyResponse(apiKeyId = "key_1", apiKey = "sk_test")

    client.createApiKey { name = "my-key" }.apiKeyId shouldBe "key_1"
  }

  "deleteApiKey delegates to apiKeys resource" {
    val client = mockTopLevelClient()
    val mockApiKeys = mockk<ApiKeyResource>()
    every { client.apiKeys } returns mockApiKeys
    coEvery { mockApiKeys.delete(any()) } returns Unit

    client.deleteApiKey("key_1")
    coVerify { mockApiKeys.delete("key_1") }
  }
})
