package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.User
import kz.scope.hiremeserver.payload.ApiResponse
import kz.scope.hiremeserver.payload.UserSummary
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.security.CurrentUser
import kz.scope.hiremeserver.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/api")
class FollowingController {
    @Autowired
    lateinit var userRepository: UserRepository

    @GetMapping("/{username}/following")
    @PreAuthorize("hasRole('USER')")
    fun getFollowingUsers(@PathVariable(value = "username") username: String): List<UserSummary> {
        // working with the target user
        val targetUser = userRepository.findByUsername(username)

        if (targetUser != null) {
            // the target user exists
            val followingList: MutableList<UserSummary> = ArrayList()
            for (followingUser in targetUser.following) {
                followingList.add(UserSummary(
                        id = followingUser.id,
                        username = followingUser.username,
                        fullname = followingUser.fullname
                ))
            }
            return followingList
        } else throw ResourceNotFoundException("User", "username", username)
    }

    @GetMapping("/{username}/followed")
    @PreAuthorize("hasRole('USER')")
    fun getFollowedUsers(@PathVariable(value = "username") username: String): List<UserSummary> {
        val targetUser = userRepository.findByUsername(username)

        if (targetUser != null) {
            // the target user exists
            val followingList: MutableList<UserSummary> = ArrayList()
            for (followingUser in userRepository.findByFollowing(targetUser)) {
                followingList.add(UserSummary(
                        id = followingUser.id,
                        username = followingUser.username,
                        fullname = followingUser.fullname
                ))
            }
            return followingList
        } else throw ResourceNotFoundException("User", "username", username)
    }

    @PostMapping("/{username}/follow")
    @PreAuthorize("hasRole('USER')")
    fun follow(@CurrentUser currentUser: UserPrincipal,
               @PathVariable(value = "username") username: String): ResponseEntity<*> {
        // getting current user of class User
        val currentUserId = currentUser.id
        val currentUserOptional = userRepository.findById(currentUserId)
        val currUser: User

        if (currentUserOptional.isPresent) {
            currUser = currentUserOptional.get()
        } else {
            throw ResourceNotFoundException("User", "id", currentUserId)
        }

        // working with the target user
        val targetUser = userRepository.findByUsername(username)

        if (targetUser != null) {
            if (targetUser.id == currUser.id) {
                return ResponseEntity.badRequest().body("You cannot follow yourself")
            }
            if (!currUser.following.add(targetUser)) {
                // already following
                return ResponseEntity.badRequest().body("You already follow this user")
            }

            val result = userRepository.save(currUser)

            val location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/users/{username}")
                    .buildAndExpand(result.username).toUri()

            return ResponseEntity.created(location).body(ApiResponse(true, "You are now following this user"))
        } else throw ResourceNotFoundException("User", "username", username)
    }
}