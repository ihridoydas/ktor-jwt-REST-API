package info.hridoydas.service

import com.auth0.jwt.interfaces.DecodedJWT
import info.hridoydas.model.User
import info.hridoydas.repository.RefreshTokenRepository
import info.hridoydas.repository.UserRepository
import info.hridoydas.routing.request.LoginRequest
import info.hridoydas.routing.response.AuthResponse
import java.util.*

class UserService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun findAll(): List<User> =
        userRepository.findAll()

    fun findById(id: String): User? =
        userRepository.findById(id = UUID.fromString(id))

    fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)

    fun save(user: User): User? {
        val foundUser = findByUsername(username = user.username)
        return if (foundUser == null) {
            userRepository.save(user)
            user
        } else null
    }

    fun authenticate(loginRequest: LoginRequest): AuthResponse? {
        val username = loginRequest.username
        val foundUser = userRepository.findByUsername(username)

        return if (foundUser != null && foundUser.password == loginRequest.password) {
            val accessToken = jwtService.createAccessToken(username, foundUser.role)
            val refreshToken = jwtService.createRefreshToken(username, foundUser.role)

            refreshTokenRepository.save(refreshToken, username)
            AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } else null
    }

    fun refreshToken(token: String): String? {
        val decodeRefreshToken = verifyRefreshToken(token)
        val persistedUsername = refreshTokenRepository.findUsernameByToken(token)

        return if (decodeRefreshToken != null && persistedUsername != null) {
            val foundUser = userRepository.findByUsername(persistedUsername)
            val usernameFromRefreshToken = decodeRefreshToken.getClaim("username").asString()

            if (foundUser != null && usernameFromRefreshToken == foundUser.username) {
                jwtService.createAccessToken(persistedUsername, foundUser.role)
            } else
                null

        } else null
    }

    private fun verifyRefreshToken(token: String): DecodedJWT? {
        val decodedJWT = decodedJWT(token)
        return decodedJWT?.let {
            val audienceMatches = jwtService.audienceMatches(it.audience.first())
            if (audienceMatches) {
                decodedJWT
            } else
                null
        }

    }

    private fun decodedJWT(token: String) = try {
        jwtService.jwtVerifier.verify(token)
    } catch (ex: Exception) {
        null
    }

}