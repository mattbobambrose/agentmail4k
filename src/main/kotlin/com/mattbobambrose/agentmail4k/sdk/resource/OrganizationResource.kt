package com.mattbobambrose.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.mattbobambrose.agentmail4k.sdk.model.Organization

class OrganizationResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  suspend fun get(): Organization {
    return client.get(basePath).body()
  }
}
