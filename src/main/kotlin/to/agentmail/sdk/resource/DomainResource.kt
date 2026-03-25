package to.agentmail.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.encodeURLPathPart
import to.agentmail.sdk.builder.CreateDomainBuilder
import to.agentmail.sdk.builder.ListDomainsBuilder
import to.agentmail.sdk.builder.UpdateDomainBuilder
import to.agentmail.sdk.model.Domain
import to.agentmail.sdk.model.DomainList

class DomainResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  suspend fun list(block: ListDomainsBuilder.() -> Unit = {}): DomainList {
    val params = ListDomainsBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  suspend fun create(block: CreateDomainBuilder.() -> Unit): Domain {
    val body = CreateDomainBuilder().apply(block).build()
    return client.post(basePath) {
      setBody(body)
    }.body()
  }

  suspend fun get(domainId: String): Domain {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    return client.get("$basePath/${domainId.encodeURLPathPart()}").body()
  }

  suspend fun update(domainId: String, block: UpdateDomainBuilder.() -> Unit): Domain {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    val body = UpdateDomainBuilder().apply(block).build()
    return client.patch("$basePath/${domainId.encodeURLPathPart()}") {
      setBody(body)
    }.body()
  }

  suspend fun delete(domainId: String) {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    client.delete("$basePath/${domainId.encodeURLPathPart()}")
  }

  suspend fun verify(domainId: String) {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    client.post("$basePath/${domainId.encodeURLPathPart()}/verify")
  }

  suspend fun getZoneFile(domainId: String): ByteArray {
    require(domainId.isNotEmpty()) { "Domain ID must not be empty." }
    return client.get("$basePath/${domainId.encodeURLPathPart()}/zone-file").body()
  }
}
