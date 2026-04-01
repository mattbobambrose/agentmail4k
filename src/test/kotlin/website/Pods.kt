@file:Suppress("unused")

package website

import com.mattbobambrose.agentmail4k.dsl.createPod
import com.mattbobambrose.agentmail4k.dsl.deletePod
import com.mattbobambrose.agentmail4k.dsl.getPod
import com.mattbobambrose.agentmail4k.dsl.listPods
import com.mattbobambrose.agentmail4k.sdk.AgentMailClient

// --8<-- [start:create-pod]
suspend fun createPodExample() {
    val client = AgentMailClient()
    val pod = client.createPod()
    println("Pod ID: ${pod.podId}")
    client.close()
}
// --8<-- [end:create-pod]

// --8<-- [start:list-pods]
suspend fun listPodsExample() {
    val client = AgentMailClient()
    val result = client.listPods {
        limit = 10
    }
    for (pod in result.pods) {
        println("Pod: ${pod.podId}")
    }
    client.close()
}
// --8<-- [end:list-pods]

// --8<-- [start:get-pod]
suspend fun getPodExample() {
    val client = AgentMailClient()
    val pod = client.getPod("pod-id")
    println("Pod ID: ${pod.podId}")
    client.close()
}
// --8<-- [end:get-pod]

// --8<-- [start:delete-pod]
suspend fun deletePodExample() {
    val client = AgentMailClient()
    client.deletePod("pod-id")
    println("Pod deleted")
    client.close()
}
// --8<-- [end:delete-pod]
