package info.hridoydas.repository

import info.hridoydas.model.User
import java.util.UUID

class UserRepository {

    private val users = mutableListOf(
        User(
            id = UUID.randomUUID(),
            username = "admin",
            password = "password",
            role = "ADMIN"
        )
    )

    fun findAll(): List<User> = users

    fun findById(id: UUID): User? =
        users.firstOrNull { it.id == id }

    fun findByUsername(username: String): User? =
        users.firstOrNull { it.username == username }

    fun save(user: User): Boolean =
        users.add(user)

}