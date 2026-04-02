package com.agentmail4k.sdk

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import com.agentmail4k.sdk.builder.CreateDomainBuilder
import com.agentmail4k.sdk.builder.CreateListEntryBuilder
import com.agentmail4k.sdk.builder.CreateWebhookBuilder
import com.agentmail4k.sdk.builder.DeleteThreadBuilder
import com.agentmail4k.sdk.builder.ForwardMessageBuilder
import com.agentmail4k.sdk.builder.ListInboxesBuilder
import com.agentmail4k.sdk.builder.ListMessagesBuilder
import com.agentmail4k.sdk.builder.ListThreadsBuilder
import com.agentmail4k.sdk.builder.QueryMetricsBuilder
import com.agentmail4k.sdk.builder.SendDraftBuilder
import com.agentmail4k.sdk.builder.SendMessageBuilder
import com.agentmail4k.sdk.builder.UpdateInboxBuilder
import com.agentmail4k.sdk.builder.UpdateMessageBuilder
import com.agentmail4k.sdk.builder.UpdateWebhookBuilder
import com.agentmail4k.sdk.model.MetricsPeriod
import com.agentmail4k.sdk.model.WebhookEvent

class BuilderTest : StringSpec({

  // --- SendMessageBuilder ---

  "SendMessageBuilder should throw when to list is empty" {
    val builder = SendMessageBuilder()
    shouldThrow<IllegalStateException> {
      builder.build()
    }.message shouldBe "At least one recipient (to) is required"
  }

  "SendMessageBuilder should build successfully with non-empty to list" {
    val builder = SendMessageBuilder().apply {
      to = listOf("alice@example.com")
      subject = "Hello"
      text = "Hi there"
    }
    val request = builder.build()
    request.to shouldBe listOf("alice@example.com")
    request.subject shouldBe "Hello"
    request.text shouldBe "Hi there"
  }

  // --- ForwardMessageBuilder ---

  "ForwardMessageBuilder should throw when to list is empty" {
    val builder = ForwardMessageBuilder()
    shouldThrow<IllegalStateException> {
      builder.build()
    }.message shouldBe "At least one recipient (to) is required for forwarding"
  }

  // --- CreateDomainBuilder ---

  "CreateDomainBuilder should throw when name is null" {
    val builder = CreateDomainBuilder()
    shouldThrow<IllegalArgumentException> {
      builder.build()
    }.message shouldBe "Domain name is required"
  }

  "CreateDomainBuilder should build successfully when name is set" {
    val builder = CreateDomainBuilder().apply { name = "example.com" }
    val request = builder.build()
    request.name shouldBe "example.com"
  }

  // --- CreateWebhookBuilder ---

  "CreateWebhookBuilder should throw when url is null" {
    val builder = CreateWebhookBuilder()
    shouldThrow<IllegalArgumentException> {
      builder.build()
    }.message shouldBe "Webhook URL is required"
  }

  "CreateWebhookBuilder should build successfully when url is set" {
    val builder = CreateWebhookBuilder().apply { url = "https://example.com/hook" }
    val request = builder.build()
    request.url shouldBe "https://example.com/hook"
    request.events shouldBe emptyList()
  }

  // --- CreateListEntryBuilder ---

  "CreateListEntryBuilder should throw when entry is null" {
    val builder = CreateListEntryBuilder()
    shouldThrow<IllegalArgumentException> {
      builder.build()
    }.message shouldBe "List entry value is required"
  }

  "CreateListEntryBuilder should build successfully when entry is set" {
    val builder = CreateListEntryBuilder().apply { entry = "spam@example.com" }
    val request = builder.build()
    request.entry shouldBe "spam@example.com"
  }

  // --- UpdateInboxBuilder ---

  "UpdateInboxBuilder should throw when displayName is null" {
    val builder = UpdateInboxBuilder()
    shouldThrow<IllegalArgumentException> {
      builder.build()
    }.message shouldBe "displayName is required for updating an inbox"
  }

  "UpdateInboxBuilder should build successfully when displayName is set" {
    val builder = UpdateInboxBuilder().apply { displayName = "My Inbox" }
    val request = builder.build()
    request.displayName shouldBe "My Inbox"
  }

  // --- ListInboxesBuilder query params ---

  "ListInboxesBuilder should produce empty map when no params set" {
    val builder = ListInboxesBuilder()
    builder.toQueryParams().shouldBeEmpty()
  }

  "ListInboxesBuilder should produce correct query params" {
    val builder = ListInboxesBuilder().apply {
      limit = 25
      pageToken = "token123"
      ascending = true
    }
    builder.toQueryParams() shouldContainExactly mapOf(
      "limit" to "25",
      "page_token" to "token123",
      "ascending" to "true",
    )
  }

  // --- ListThreadsBuilder query params ---

  "ListThreadsBuilder should produce empty map when no params set" {
    val builder = ListThreadsBuilder()
    builder.toQueryParams().shouldBeEmpty()
  }

  "ListThreadsBuilder should produce all query params correctly" {
    val builder = ListThreadsBuilder().apply {
      limit = 10
      pageToken = "abc"
      labels = listOf("inbox", "important")
      before = "2026-01-01"
      after = "2025-01-01"
      ascending = false
      includeSpam = true
      includeBlocked = false
      includeTrash = true
    }
    builder.toQueryParams() shouldContainExactly mapOf(
      "limit" to "10",
      "page_token" to "abc",
      "labels" to "inbox,important",
      "before" to "2026-01-01",
      "after" to "2025-01-01",
      "ascending" to "false",
      "include_spam" to "true",
      "include_blocked" to "false",
      "include_trash" to "true",
    )
  }

  // --- QueryMetricsBuilder period enum mapping ---

  "QueryMetricsBuilder should map HOUR period correctly" {
    val builder = QueryMetricsBuilder().apply { period = MetricsPeriod.HOUR }
    builder.toQueryParams()["period"] shouldBe "hour"
  }

  "QueryMetricsBuilder should map DAY period correctly" {
    val builder = QueryMetricsBuilder().apply { period = MetricsPeriod.DAY }
    builder.toQueryParams()["period"] shouldBe "day"
  }

  "QueryMetricsBuilder should map WEEK period correctly" {
    val builder = QueryMetricsBuilder().apply { period = MetricsPeriod.WEEK }
    builder.toQueryParams()["period"] shouldBe "week"
  }

  "QueryMetricsBuilder should map MONTH period correctly" {
    val builder = QueryMetricsBuilder().apply { period = MetricsPeriod.MONTH }
    builder.toQueryParams()["period"] shouldBe "month"
  }

  "QueryMetricsBuilder should produce all query params" {
    val builder = QueryMetricsBuilder().apply {
      eventTypes = "sent,received"
      start = "2025-01-01"
      end = "2025-12-31"
      period = MetricsPeriod.DAY
      limit = 100
      descending = true
    }
    builder.toQueryParams() shouldContainExactly mapOf(
      "event_types" to "sent,received",
      "start" to "2025-01-01",
      "end" to "2025-12-31",
      "period" to "day",
      "limit" to "100",
      "descending" to "true",
    )
  }

  // --- DeleteThreadBuilder ---

  "DeleteThreadBuilder should produce empty map when permanent is null" {
    val builder = DeleteThreadBuilder()
    builder.toQueryParams().shouldBeEmpty()
  }

  "DeleteThreadBuilder should include permanent param when set" {
    val builder = DeleteThreadBuilder().apply { permanent = true }
    builder.toQueryParams() shouldContainExactly mapOf("permanent" to "true")
  }

  // --- ListMessagesBuilder query params ---

  "ListMessagesBuilder should produce empty map when no params set" {
    val builder = ListMessagesBuilder()
    builder.toQueryParams().shouldBeEmpty()
  }

  "ListMessagesBuilder should produce all query params correctly" {
    val builder = ListMessagesBuilder().apply {
      limit = 50
      pageToken = "tok"
      labels = listOf("inbox")
      before = "2026-01-01"
      after = "2025-01-01"
      ascending = true
      includeSpam = true
      includeBlocked = false
      includeTrash = true
    }
    builder.toQueryParams() shouldContainExactly mapOf(
      "limit" to "50",
      "page_token" to "tok",
      "labels" to "inbox",
      "before" to "2026-01-01",
      "after" to "2025-01-01",
      "ascending" to "true",
      "include_spam" to "true",
      "include_blocked" to "false",
      "include_trash" to "true",
    )
  }

  // --- CreateWebhookBuilder events vararg ---

  "CreateWebhookBuilder events(vararg) should populate events list from enum" {
    val builder = CreateWebhookBuilder().apply {
      url = "https://example.com/hook"
      events(WebhookEvent.MESSAGE_RECEIVED, WebhookEvent.MESSAGE_SENT)
    }
    val request = builder.build()
    request.events shouldBe listOf("message.received", "message.sent")
  }

  // --- UpdateWebhookBuilder events vararg ---

  "UpdateWebhookBuilder events(vararg) should populate events list from enum" {
    val builder = UpdateWebhookBuilder().apply {
      events(WebhookEvent.MESSAGE_BOUNCED, WebhookEvent.DOMAIN_VERIFIED)
    }
    val request = builder.build()
    request.events shouldBe listOf("message.bounced", "domain.verified")
  }

  // --- SendDraftBuilder ---

  "SendDraftBuilder should serialize labels correctly" {
    val builder = SendDraftBuilder().apply {
      labels = listOf("sent", "archived")
    }
    val request = builder.build()
    request.labels shouldBe listOf("sent", "archived")
  }

  "SendDraftBuilder should serialize null labels" {
    val builder = SendDraftBuilder()
    val request = builder.build()
    request.labels shouldBe null
  }

  // --- UpdateMessageBuilder ---

  "UpdateMessageBuilder should set labels for full replacement" {
    val builder = UpdateMessageBuilder().apply {
      labels = listOf("read", "important")
    }
    val request = builder.build()
    request.labels shouldBe listOf("read", "important")
    request.addLabels shouldBe null
    request.removeLabels shouldBe null
  }

  "UpdateMessageBuilder should add labels incrementally via varargs" {
    val builder = UpdateMessageBuilder().apply {
      addLabels("read", "important")
    }
    val request = builder.build()
    request.labels shouldBe null
    request.addLabels shouldBe listOf("read", "important")
    request.removeLabels shouldBe null
  }

  "UpdateMessageBuilder should remove labels incrementally via varargs" {
    val builder = UpdateMessageBuilder().apply {
      removeLabels("unread", "new")
    }
    val request = builder.build()
    request.labels shouldBe null
    request.addLabels shouldBe null
    request.removeLabels shouldBe listOf("unread", "new")
  }

  "UpdateMessageBuilder should support add and remove together" {
    val builder = UpdateMessageBuilder().apply {
      addLabels("read")
      removeLabels("unread")
    }
    val request = builder.build()
    request.labels shouldBe null
    request.addLabels shouldBe listOf("read")
    request.removeLabels shouldBe listOf("unread")
  }

  "UpdateMessageBuilder should support mixing labels with addLabels and removeLabels" {
    val builder = UpdateMessageBuilder().apply {
      labels = listOf("base")
      addLabels("extra")
      removeLabels("old")
    }
    val request = builder.build()
    request.labels shouldBe listOf("base")
    request.addLabels shouldBe listOf("extra")
    request.removeLabels shouldBe listOf("old")
  }

  "UpdateMessageBuilder should accumulate multiple addLabels calls" {
    val builder = UpdateMessageBuilder().apply {
      addLabels("read")
      addLabels("important")
    }
    val request = builder.build()
    request.addLabels shouldBe listOf("read", "important")
  }

  "UpdateMessageBuilder should default all fields to null" {
    val builder = UpdateMessageBuilder()
    val request = builder.build()
    request.labels shouldBe null
    request.addLabels shouldBe null
    request.removeLabels shouldBe null
  }
})
