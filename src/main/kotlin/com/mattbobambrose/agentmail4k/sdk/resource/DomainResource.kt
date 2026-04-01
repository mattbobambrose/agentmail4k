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
import com.mattbobambrose.agentmail4k.sdk.builder.CreateDomainBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListDomainsBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateDomainBuilder
import com.mattbobambrose.agentmail4k.sdk.model.Domain
import com.mattbobambrose.agentmail4k.sdk.model.DomainList

/** Provides operations for managing custom domains: list, create, get, update, delete, verify, and retrieve zone files. */
class DomainResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  /** Lists custom domains with optional pagination. */
  suspend fun list(block: ListDomainsBuilder.() -> Unit = {}): DomainList {
    val params = ListDomainsBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  /** Creates a new custom domain. */
  suspend fun create(block: CreateDomainBuilder.() -> Unit): Domain {
    val body = CreateDomainBuilder().apply(block).build()
    return client.post(basePath) {
      setBody(body)
    }.body()
  }

  /** Retrieves a domain by ID. */
  suspend fun get(domainId: String): Domain {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    return client.get("$basePath/${domainId.encodeURLPathPart()}").body()
  }

  /** Updates a domain by ID. */
  suspend fun update(domainId: String, block: UpdateDomainBuilder.() -> Unit): Domain {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    val body = UpdateDomainBuilder().apply(block).build()
    return client.patch("$basePath/${domainId.encodeURLPathPart()}") {
      setBody(body)
    }.body()
  }

  /** Deletes a domain by ID. */
  suspend fun delete(domainId: String) {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    client.delete("$basePath/${domainId.encodeURLPathPart()}")
  }

  /** Triggers DNS verification for a domain. */
  suspend fun verify(domainId: String) {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    client.post("$basePath/${domainId.encodeURLPathPart()}/verify")
  }

  /** Retrieves the DNS zone file for a domain as raw bytes. */
  suspend fun getZoneFile(domainId: String): ByteArray {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    return client.get("$basePath/${domainId.encodeURLPathPart()}/zone-file").body()
  }
}
