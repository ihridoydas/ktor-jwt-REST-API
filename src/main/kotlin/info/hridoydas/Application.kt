package info.hridoydas

import info.hridoydas.plugins.configureSecurity
import info.hridoydas.plugins.configureSerialization
import info.hridoydas.repository.RefreshTokenRepository
import info.hridoydas.repository.UserRepository
import info.hridoydas.routing.configureRouting
import info.hridoydas.service.JwtService
import info.hridoydas.service.UserService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val userRepository = UserRepository()
    val jwtService = JwtService(this, userRepository)
    val refreshTokenRepository = RefreshTokenRepository()
    val userService = UserService(
        userRepository = userRepository,
        jwtService = jwtService,
        refreshTokenRepository = refreshTokenRepository
    )

    configureSerialization()
    configureSecurity(jwtService)
    configureRouting(userService)
}
