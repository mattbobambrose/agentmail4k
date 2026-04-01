package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient

/** Retrieves the current organization's details. */
suspend fun AgentMailClient.getOrganization() =
  organization.get()
