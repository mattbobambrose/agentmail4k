package to.agentmail.sdk.builder

import to.agentmail.sdk.AgentMailDsl

@AgentMailDsl
class ListThreadsBuilder {
  var limit: Int? = null
  var pageToken: String? = null
  var labels: List<String>? = null
  var before: String? = null
  var after: String? = null
  var ascending: Boolean? = null
  var includeSpam: Boolean? = null
  var includeBlocked: Boolean? = null
  var includeTrash: Boolean? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    limit?.let { put("limit", it.toString()) }
    pageToken?.let { put("page_token", it) }
    labels?.let { put("labels", it.joinToString(",")) }
    before?.let { put("before", it) }
    after?.let { put("after", it) }
    ascending?.let { put("ascending", it.toString()) }
    includeSpam?.let { put("include_spam", it.toString()) }
    includeBlocked?.let { put("include_blocked", it.toString()) }
    includeTrash?.let { put("include_trash", it.toString()) }
  }
}

@AgentMailDsl
class DeleteThreadBuilder {
  var permanent: Boolean? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    permanent?.let { put("permanent", it.toString()) }
  }
}
