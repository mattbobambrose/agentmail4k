package com.agentmail4k.sdk

import com.agentmail4k.dsl.bulk
import com.agentmail4k.sdk.model.ThreadList
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify

class BulkTest : StringSpec({

  "forEachThread paginates through all pages" {
    val (client, mockThreads) = mockThreadClient()
    val thread1 = testThread(threadId = "thread_1", subject = "Thread 1")
    val thread2 = testThread(threadId = "thread_2", subject = "Thread 2")

    coEvery { mockThreads.list(any()) } returnsMany listOf(
      ThreadList(count = 2, nextPageToken = "page2", threads = listOf(thread1)),
      ThreadList(count = 2, threads = listOf(thread2)),
    )

    val visited = mutableListOf<String>()

    client.bulk {
      forEachThread(inboxId = "inbox_1") { thread ->
        visited.add(thread.threadId)
      }
    }

    visited shouldBe listOf("thread_1", "thread_2")
    coVerify(exactly = 2) { mockThreads.list(any()) }
  }

  "forEachThread applies filter builder" {
    val (client, mockThreads) = mockThreadClient()

    coEvery { mockThreads.list(any()) } returns ThreadList(count = 0, threads = emptyList())

    client.bulk {
      forEachThread(
        inboxId = "inbox_1",
        filter = { limit = 10 },
      ) { }
    }

    coVerify { mockThreads.list(any()) }
  }

  "bulk send with empty recipients list returns empty results" {
    val (client, mockMessages) = mockInboxClient()

    val results = client.bulk {
      send("inbox_1", emptyList()) {
        subject = "Hello"
        text = "Hi"
      }
    }

    results shouldBe emptyList()
    coVerify(exactly = 0) { mockMessages.send(any()) }
  }
})
