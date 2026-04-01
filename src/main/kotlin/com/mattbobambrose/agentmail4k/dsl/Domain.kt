package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.CreateDomainBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListDomainsBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateDomainBuilder

/** Lists all custom domains with optional pagination. */
suspend fun AgentMailClient.listDomains(block: ListDomainsBuilder.() -> Unit = {}) =
  domains.list(block)

/** Creates a new custom domain. */
suspend fun AgentMailClient.createDomain(block: CreateDomainBuilder.() -> Unit) =
  domains.create(block)

/** Retrieves a domain by ID. */
suspend fun AgentMailClient.getDomain(domainId: String) =
  domains.get(domainId)

/** Updates a domain by ID. */
suspend fun AgentMailClient.updateDomain(domainId: String, block: UpdateDomainBuilder.() -> Unit) =
  domains.update(domainId, block)

/** Deletes a domain by ID. */
suspend fun AgentMailClient.deleteDomain(domainId: String) =
  domains.delete(domainId)

/** Triggers DNS verification for a domain. */
suspend fun AgentMailClient.verifyDomain(domainId: String) =
  domains.verify(domainId)

/** Retrieves the DNS zone file for a domain. */
suspend fun AgentMailClient.getDomainZoneFile(domainId: String) =
  domains.getZoneFile(domainId)
