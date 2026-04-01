package com.agentmail4k.dsl

import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.builder.CreateListEntryBuilder
import com.agentmail4k.sdk.builder.ListEntriesBuilder
import com.agentmail4k.sdk.model.ListDirection
import com.agentmail4k.sdk.model.ListType

/** Lists allow/block entries for a given [direction] and [type]. */
suspend fun AgentMailClient.listEntries(
  direction: ListDirection,
  type: ListType,
  block: ListEntriesBuilder.() -> Unit = {},
) = lists.list(direction, type, block)

/** Creates a new allow/block list entry. */
suspend fun AgentMailClient.createListEntry(
  direction: ListDirection,
  type: ListType,
  block: CreateListEntryBuilder.() -> Unit,
) = lists.create(direction, type, block)

/** Retrieves a specific list entry. */
suspend fun AgentMailClient.getListEntry(
  direction: ListDirection,
  type: ListType,
  entry: String,
) = lists.get(direction, type, entry)

/** Deletes a specific list entry. */
suspend fun AgentMailClient.deleteListEntry(
  direction: ListDirection,
  type: ListType,
  entry: String,
) = lists.delete(direction, type, entry)
