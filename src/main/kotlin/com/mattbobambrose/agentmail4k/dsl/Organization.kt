package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient

suspend fun AgentMailClient.getOrganization() =
  organization.get()
