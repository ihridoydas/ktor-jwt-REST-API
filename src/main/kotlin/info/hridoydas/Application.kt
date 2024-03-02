package info.hridoydas

import info.hridoydas.plugins.configureSerialization
import info.hridoydas.repository.UserRepository
import info.hridoydas.routing.configureRouting
import info.hridoydas.service.UserService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val userRepository = UserRepository()
    val userService = UserService(userRepository)
    configureSerialization()
    configureRouting(userService)
}
