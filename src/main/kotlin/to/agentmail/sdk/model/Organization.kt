package to.agentmail.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    @SerialName("updated_at") val updatedAt: Instant,
    @SerialName("created_at") val createdAt: Instant,
)
