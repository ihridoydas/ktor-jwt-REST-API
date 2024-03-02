package info.hridoydas.routing

import info.hridoydas.model.User
import info.hridoydas.routing.request.UserRequest
import info.hridoydas.routing.response.UserResponse
import info.hridoydas.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.userRoute(
    userService: UserService
) {
    post {
        val userRequest = call.receive<UserRequest>()

        val createUser = userService.save(
            user = userRequest.toModel()
        ) ?: return@post call.respond(HttpStatusCode.BadRequest)

        call.response.header(
            name = "id",
            value = createUser.id.toString()
        )

        call.respond(
            message = HttpStatusCode.Created
        )
    }

    authenticate {
        get {
            val users = userService.findAll()

            call.respond(
                message = users.map(User::toResponse)
            )
        }
    }

    authenticate("another-auth") {
        get("/{id}") {
            val id: String = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val foundUser = userService.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            if (foundUser.username != extractPrincipalUsername(call))
                return@get call.respond(HttpStatusCode.NotFound)

            call.respond(
                message = foundUser.toResponse()
            )
        }
    }
}

fun extractPrincipalUsername(call: ApplicationCall): String? =
    call.principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("username")
        ?.asString()

private fun UserRequest.toModel(): User =
    User(
        id = UUID.randomUUID(),
        username = this.username,
        password = this.password,
    )

private fun User.toResponse(): UserResponse =
    UserResponse(
        id = this.id,
        username = this.username
    )
