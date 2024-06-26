package info.hridoydas.model

import java.util.UUID

data class User(
    val id: UUID,
    val username: String,
    val password: String,
    val role: String
)
