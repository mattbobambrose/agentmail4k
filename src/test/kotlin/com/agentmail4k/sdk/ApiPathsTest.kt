package com.agentmail4k.sdk

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import com.agentmail4k.sdk.internal.ApiPaths

class ApiPathsTest : StringSpec() {
  init {
    "ApiPaths.inbox() should throw on empty inboxId" {
      shouldThrow<IllegalArgumentException> {
        ApiPaths.inbox("")
      }.message shouldBe "Inbox ID must not be empty."
    }

    "ApiPaths.pod() should throw on empty podId" {
      shouldThrow<IllegalArgumentException> {
        ApiPaths.pod("")
      }.message shouldBe "Pod ID must not be empty."
    }

    "ApiPaths.inbox() should URL-encode the inboxId" {
      val result = ApiPaths.inbox("inbox with spaces")
      result shouldBe "v0/inboxes/inbox%20with%20spaces"
    }

    "ApiPaths.pod() should URL-encode the podId" {
      val result = ApiPaths.pod("pod/special")
      result shouldBe "v0/pods/pod%2Fspecial"
    }

    "ApiPaths.inbox() should return correct path for simple id" {
      val result = ApiPaths.inbox("inbox_123")
      result shouldBe "v0/inboxes/inbox_123"
    }

    "ApiPaths.pod() should return correct path for simple id" {
      val result = ApiPaths.pod("pod_42")
      result shouldBe "v0/pods/pod_42"
    }
  }
}
