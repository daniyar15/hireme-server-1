package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.payload.*
import kz.scope.hiremeserver.repository.PollRepository
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.repository.VoteRepository
import kz.scope.hiremeserver.security.CurrentUser
import kz.scope.hiremeserver.security.UserPrincipal
import kz.scope.hiremeserver.service.PollService
import kz.scope.hiremeserver.util.AppConstants
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

private val logger1 = LoggerFactory.getLogger(UserController::class.java)

@RestController
@RequestMapping("/api")
open class UserController {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var pollRepository: PollRepository

    @Autowired
    lateinit var voteRepository: VoteRepository

    @Autowired
    lateinit var pollService: PollService

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser currentUser: UserPrincipal): UserSummary {
        return UserSummary(currentUser.id, currentUser.username, currentUser.name)
    }

    @GetMapping("/user/checkUsernameAvailability")
    fun checkUsernameAvailability(@RequestParam(value = "username") username: String): UserIdentityAvailability {
        val isAvailable = !userRepository.existsByUsername(username)
        return UserIdentityAvailability(isAvailable)
    }

    @GetMapping("/user/checkEmailAvailability")
    fun checkEmailAvailability(@RequestParam(value = "email") email: String): UserIdentityAvailability {
        val isAvailable = !userRepository.existsByEmail(email)
        return UserIdentityAvailability(isAvailable)
    }

    @GetMapping("/users/{username}")
    fun getUserProfile(@PathVariable(value = "username") username: String): UserProfile {
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User", "username", username) }

        val pollCount = pollRepository.countByCreatedBy(user.id)
        val voteCount = voteRepository.countByUserId(user.id)

        return UserProfile(user.id, user.username, user.name, user.createdAt, pollCount, voteCount)
    }

    @GetMapping("/users/{username}/polls")
    fun getPollsCreatedBy(@PathVariable(value = "username") username: String,
                          @CurrentUser currentUser: UserPrincipal,
                          @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) page: Int,
                          @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) size: Int): PagedResponse<PollResponse> {
        return pollService.getPollsCreatedBy(username, currentUser, page, size)
    }


    @GetMapping("/users/{username}/votes")
    fun getPollsVotedBy(@PathVariable(value = "username") username: String,
                        @CurrentUser currentUser: UserPrincipal,
                        @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) page: Int,
                        @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) size: Int): PagedResponse<PollResponse> {
        return pollService.getPollsVotedBy(username, currentUser, page, size)
    }

}
