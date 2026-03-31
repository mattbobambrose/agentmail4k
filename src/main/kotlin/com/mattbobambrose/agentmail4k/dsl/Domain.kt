package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.CreateDomainBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListDomainsBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateDomainBuilder

suspend fun AgentMailClient.listDomains(block: ListDomainsBuilder.() -> Unit = {}) =
  domains.list(block)

suspend fun AgentMailClient.createDomain(block: CreateDomainBuilder.() -> Unit) =
  domains.create(block)

suspend fun AgentMailClient.getDomain(domainId: String) =
  domains.get(domainId)

suspend fun AgentMailClient.updateDomain(domainId: String, block: UpdateDomainBuilder.() -> Unit) =
  domains.update(domainId, block)

suspend fun AgentMailClient.deleteDomain(domainId: String) =
  domains.delete(domainId)

suspend fun AgentMailClient.verifyDomain(domainId: String) =
  domains.verify(domainId)

suspend fun AgentMailClient.getDomainZoneFile(domainId: String) =
  domains.getZoneFile(domainId)
