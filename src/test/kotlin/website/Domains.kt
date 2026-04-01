@file:Suppress("unused")

package website

import com.mattbobambrose.agentmail4k.dsl.createDomain
import com.mattbobambrose.agentmail4k.dsl.deleteDomain
import com.mattbobambrose.agentmail4k.dsl.getDomain
import com.mattbobambrose.agentmail4k.dsl.getDomainZoneFile
import com.mattbobambrose.agentmail4k.dsl.listDomains
import com.mattbobambrose.agentmail4k.dsl.updateDomain
import com.mattbobambrose.agentmail4k.dsl.verifyDomain
import com.mattbobambrose.agentmail4k.sdk.AgentMailClient

// --8<-- [start:create-domain]
suspend fun createDomainExample() {
    val client = AgentMailClient()
    val domain = client.createDomain {
        name = "example.com"
    }
    println("Domain ID: ${domain.domainId}")
    println("Verified: ${domain.verified}")
    client.close()
}
// --8<-- [end:create-domain]

// --8<-- [start:list-domains]
suspend fun listDomainsExample() {
    val client = AgentMailClient()
    val result = client.listDomains {
        limit = 10
    }
    for (domain in result.domains) {
        println("${domain.name}: verified=${domain.verified}")
    }
    client.close()
}
// --8<-- [end:list-domains]

// --8<-- [start:get-domain]
suspend fun getDomainExample() {
    val client = AgentMailClient()
    val domain = client.getDomain("domain-id")
    println("Name: ${domain.name}")
    println("Verified: ${domain.verified}")
    client.close()
}
// --8<-- [end:get-domain]

// --8<-- [start:update-domain]
suspend fun updateDomainExample() {
    val client = AgentMailClient()
    val updated = client.updateDomain("domain-id") {
        name = "newname.com"
    }
    println("Updated: ${updated.name}")
    client.close()
}
// --8<-- [end:update-domain]

// --8<-- [start:verify-domain]
suspend fun verifyDomainExample() {
    val client = AgentMailClient()
    client.verifyDomain("domain-id")
    println("Verification request sent")
    client.close()
}
// --8<-- [end:verify-domain]

// --8<-- [start:zone-file]
suspend fun zoneFileExample() {
    val client = AgentMailClient()
    val zoneFile = client.getDomainZoneFile("domain-id")
    println(zoneFile)
    client.close()
}
// --8<-- [end:zone-file]

// --8<-- [start:delete-domain]
suspend fun deleteDomainExample() {
    val client = AgentMailClient()
    client.deleteDomain("domain-id")
    println("Domain deleted")
    client.close()
}
// --8<-- [end:delete-domain]
