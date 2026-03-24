package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import to.agentmail.sdk.model.Organization

class OrganizationResource internal constructor(
    private val client: HttpClient,
    private val basePath: String,
) {
    suspend fun get(): Organization {
        return client.get(basePath).body()
    }
}
