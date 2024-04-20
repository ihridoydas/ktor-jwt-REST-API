package info.hridoydas.util

import info.hridoydas.plugins.RoleBasedAuthorizationPlugin
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.authorized(
    vararg hasAnyRole: String,
    build: Route.() -> Unit
){
   install(RoleBasedAuthorizationPlugin){roles = hasAnyRole.toSet() }
    build()
}