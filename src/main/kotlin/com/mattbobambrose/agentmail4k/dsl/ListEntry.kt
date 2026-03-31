package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.CreateListEntryBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListEntriesBuilder
import com.mattbobambrose.agentmail4k.sdk.model.ListDirection
import com.mattbobambrose.agentmail4k.sdk.model.ListType

suspend fun AgentMailClient.listEntries(
  direction: ListDirection,
  type: ListType,
  block: ListEntriesBuilder.() -> Unit = {},
) = lists.list(direction, type, block)

suspend fun AgentMailClient.createListEntry(
  direction: ListDirection,
  type: ListType,
  block: CreateListEntryBuilder.() -> Unit,
) = lists.create(direction, type, block)

suspend fun AgentMailClient.getListEntry(
  direction: ListDirection,
  type: ListType,
  entry: String,
) = lists.get(direction, type, entry)

suspend fun AgentMailClient.deleteListEntry(
  direction: ListDirection,
  type: ListType,
  entry: String,
) = lists.delete(direction, type, entry)
