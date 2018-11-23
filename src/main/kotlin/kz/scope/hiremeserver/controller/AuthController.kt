package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.AppException
import kz.scope.hiremeserver.model.RoleName
import kz.scope.hiremeserver.model.User
import kz.scope.hiremeserver.model.UserInfo
import kz.scope.hiremeserver.payload.*
import kz.scope.hiremeserver.repository.RoleRepository
import kz.scope.hiremeserver.repository.UserInfoRepository
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.Instant
import java.time.Instant.now
import javax.validation.Valid

/**
 * Created by scope team on 02/08/17.
 */
@RestController
@RequestMapping("/api/auth")
class AuthController {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userInfoRepository: UserInfoRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var tokenProvider: JwtTokenProvider

    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*> {

        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.usernameOrEmail,
                        loginRequest.password
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val jwt = tokenProvider.generateToken(authentication)
        return ResponseEntity.ok(JwtAuthenticationResponse(jwt))
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<*> {
        if (userRepository.existsByUsername(signUpRequest.username)) {
            return ResponseEntity(ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST)
        }

        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity(ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST)
        }

        // Creating user's account
        val user = User(signUpRequest.fullname, signUpRequest.username,
                signUpRequest.email, signUpRequest.password)

        user.password = passwordEncoder.encode(user.password)

        val userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow { AppException("User Role not set.") }

        user.roles = setOf(userRole)
        user.userInfo = UserInfo(
                "",
                "",
                "",
                "",
                "",
                false,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                now())

        userInfoRepository.save(user.userInfo)
        val result = userRepository.save(user)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.username).toUri()

        return ResponseEntity.created(location).body(ApiResponse(true, "User registered successfully"))
    }
}