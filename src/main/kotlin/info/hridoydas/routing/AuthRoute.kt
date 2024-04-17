package info.hridoydas.routing

import info.hridoydas.routing.request.LoginRequest
import info.hridoydas.routing.request.RefreshTokenRequest
import info.hridoydas.routing.response.AuthResponse
import info.hridoydas.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(
    userService: UserService
) {
    post {
        val loginRequest = call.receive<LoginRequest>()

        val authResponse: AuthResponse? = userService.authenticate(loginRequest)

        authResponse?.let {
            call.respond(authResponse)
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }

    post("/refresh") {
        val request = call.receive<RefreshTokenRequest>()

        val newAccessToken: String? = userService.refreshToken(request.token)

        newAccessToken?.let {
            call.respond(
                RefreshTokenRequest(it)
            )
        } ?: call.respond(
            message = HttpStatusCode.Unauthorized
        )
    }

}