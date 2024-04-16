package info.hridoydas.routing

import info.hridoydas.routing.request.LoginRequest
import info.hridoydas.service.JwtService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(
    jwtService: JwtService
) {
    post {
        val loginRequest = call.receive<LoginRequest>()

        val token = jwtService.createJwtToken(loginRequest)

        token?.let {
            call.respond(hashMapOf("token" to it))
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }

}