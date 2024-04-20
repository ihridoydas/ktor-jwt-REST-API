package info.hridoydas.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import info.hridoydas.repository.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtService(
    private val application: Application,
    private val userRepository: UserRepository
) {

    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")
    val realm = getConfigProperty("jwt.realm")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    fun createAccessToken(username: String, role: String): String =
        createJwtToken(username, role, 3_600_00)

    fun createRefreshToken(username: String, role: String): String =
        createJwtToken(username, role, 86_400_000)


    private fun createJwtToken(
        username: String,
        role: String,
        expireIn: Int
    ): String =
        JWT
            .create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + expireIn))
            .sign(Algorithm.HMAC256(secret))

    fun customValidator(credential: JWTCredential): JWTPrincipal? {
        val username = extractUsername(credential)
        val foundUser = username?.let(userRepository::findByUsername)

        return foundUser?.let {
            if (audienceMatches(credential)) {
                JWTPrincipal(credential.payload)
            } else null
        }
    }

    fun audienceMatches(audience: String): Boolean =
        this.audience == audience


    private fun audienceMatches(credential: JWTCredential): Boolean =
        credential.payload.audience.contains(audience)

    private fun extractUsername(credential: JWTCredential): String? =
        credential.payload.getClaim("username").asString()

    private fun getConfigProperty(path: String) =
        application.environment.config.property(path).getString()
}