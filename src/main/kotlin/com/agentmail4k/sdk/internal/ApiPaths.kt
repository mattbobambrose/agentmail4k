package com.agentmail4k.sdk.internal

import io.ktor.http.encodeURLPathPart

/**
 * Centralized constants and path builders for all AgentMail API v0 endpoints.
 * Path-building functions validate IDs and URL-encode path segments.
 */
internal object ApiPaths {
  const val INBOXES = "v0/inboxes"
  const val THREADS = "v0/threads"
  const val DRAFTS = "v0/drafts"
  const val DOMAINS = "v0/domains"
  const val PODS = "v0/pods"
  const val WEBHOOKS = "v0/webhooks"
  const val LISTS = "v0/lists"
  const val METRICS = "v0/metrics"
  const val API_KEYS = "v0/api-keys"
  const val ORGANIZATIONS = "v0/organizations"

  /** Returns the API path for a specific inbox, validating the ID is non-empty. */
  fun inbox(inboxId: String): String {
    require(inboxId.isNotEmpty()) { "Inbox ID must not be empty." }
    return "$INBOXES/${inboxId.encodeURLPathPart()}"
  }

  /** Returns the API path for a specific pod, validating the ID is non-empty. */
  fun pod(podId: String): String {
    require(podId.isNotEmpty()) { "Pod ID must not be empty." }
    return "$PODS/${podId.encodeURLPathPart()}"
  }
}
