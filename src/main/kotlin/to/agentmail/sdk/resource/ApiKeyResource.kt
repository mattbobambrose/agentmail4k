package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import to.agentmail.sdk.builder.CreateApiKeyBuilder
import to.agentmail.sdk.builder.ListApiKeysBuilder
import to.agentmail.sdk.model.ApiKeyList
import to.agentmail.sdk.model.CreateApiKeyResponse

class ApiKeyResource internal constructor(
    private val client: HttpClient,
    private val basePath: String,
) {
    suspend fun list(block: ListApiKeysBuilder.() -> Unit = {}): ApiKeyList {
        val params = ListApiKeysBuilder().apply(block).toQueryParams()
        return client.get(basePath) {
            params.forEach { (k, v) -> parameter(k, v) }
        }.body()
    }

    suspend fun create(block: CreateApiKeyBuilder.() -> Unit = {}): CreateApiKeyResponse {
        val body = CreateApiKeyBuilder().apply(block).build()
        return client.post(basePath) {
            setBody(body)
        }.body()
    }

    suspend fun delete(apiKeyId: String) {
        client.delete("$basePath/$apiKeyId")
    }
}
