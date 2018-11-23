package kz.scope. hiremeserver.controller

import com.google.gson.Gson
import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Log
import kz.scope.hiremeserver.model.UserInfo
import kz.scope.hiremeserver.payload.*
import kz.scope.hiremeserver.repository.LogRepository
import kz.scope.hiremeserver.repository.UserInfoRepository
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.security.CurrentUser
import kz.scope.hiremeserver.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private val logger1 = LoggerFactory.getLogger(UserController::class.java)

@RestController
@RequestMapping("/api")
class UserController {

    @Autowired
    lateinit var logRepository: LogRepository

    var gson = Gson()

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userInfoRepository: UserInfoRepository

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser currentUser: UserPrincipal): UserSummary {

        logRepository.save(Log(
                controller = "UserController",
                methodName = "getCurrentUser",
                httpMethod = "GET",
                urlMapping = "/user/me",
                protected = true,
                requestBody = "{}",
                requestParam = "{}")
        )

        return UserSummary(currentUser.id, currentUser.username, currentUser.fullname)
    }

    //find the user in the DB and set new value to its userInfo
    @PostMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun postCurrentUserProfile(@CurrentUser currentUser: UserPrincipal, @RequestBody userProfile: UserProfile): ResponseEntity<*> {

        logRepository.save(Log(
                controller = "UserController",
                methodName = "postCurrentUserProfile",
                httpMethod = "POST",
                urlMapping = "/user/me",
                protected = true,
                requestBody = gson.toJson(userProfile),
                requestParam = "{}")
        )

        val user = userRepository.findByUsername(userProfile.username)

        if (user == null){
            return ResponseEntity(ApiResponse(false, "No such user"), HttpStatus.EXPECTATION_FAILED)
        } else if (!user.username.equals(currentUser.username)){
            return ResponseEntity(ApiResponse(false, "You cannot edit user information for other user"), HttpStatus.EXPECTATION_FAILED)
        } else {

            user.userInfo.location = userProfile.location
            user.userInfo.university = userProfile.education.university
            user.userInfo.graduation = userProfile.education.graduation
            user.userInfo.major = userProfile.education.major
            user.userInfo.degree = userProfile.education.degree
            user.userInfo.hidden = userProfile.hidden
            user.userInfo.strongSkillName = userProfile.strong_skill.name
            user.userInfo.strongSkilldescription = userProfile.strong_skill.description
            user.userInfo.company = userProfile.employment.company
            user.userInfo.currentRole = userProfile.employment.role
            user.userInfo.referenceName = userProfile.employment.reference.name
            user.userInfo.referenceNumber = userProfile.employment.reference.number
            user.userInfo.skills = userProfile.skills
            user.userInfo.github = userProfile.urls.github
            user.userInfo.linked_in = userProfile.urls.linked_in
            user.userInfo.web = userProfile.urls.web
            user.userInfo.avatar_url = userProfile.avatar_url

            userInfoRepository.save(user.userInfo)
            val result = userRepository.save(user)

            val location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/users/{username}")
                    .buildAndExpand(result.username).toUri()

            return ResponseEntity.created(location).body(ApiResponse(true, "User profile edited successfully"))
        }
    }


    //find the user in the DB and return its userInfo
    @GetMapping("/user/me/profile")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUserProfile(@CurrentUser currentUser: UserPrincipal) : UserProfile{

        logRepository.save(Log(
                controller = "UserController",
                methodName = "getCurrentUserProfile",
                httpMethod = "GET",
                urlMapping = "/user/me/profile",
                protected = true,
                requestBody = "{}",
                requestParam = "{}")
        )

        val user = userRepository.findByUsername(currentUser.username)
        if (user != null) {
            return UserProfile(
                    user.username,
                    user.fullname,
                    user.userInfo.location,
                    Education(
                            user.userInfo.university,
                            user.userInfo.graduation,
                            user.userInfo.major,
                            user.userInfo.degree),
                    user.userInfo.hidden,
                    StrongSkill(
                            user.userInfo.strongSkillName,
                            user.userInfo.strongSkilldescription
                    ),
                    Urls(
                            user.userInfo.github,
                            user.userInfo.linked_in,
                            user.userInfo.web
                    ),
                    user.userInfo.skills,
                    Employment(
                            user.userInfo.company,
                            user.userInfo.currentRole,
                            Reference(
                                    user.userInfo.referenceName,
                                    user.userInfo.referenceNumber
                            )
                    ),
                    user.userInfo.avatar_url,
                    user.userInfo.createdAt
            )
        } else throw ResourceNotFoundException("Profile", "username", currentUser.username)
    }

    @GetMapping("/user/checkUsernameAvailability")
    fun checkUsernameAvailability(@RequestParam(value = "username") username: String): UserIdentityAvailability {

        logRepository.save(Log(
                controller = "UserController",
                methodName = "checkUsernameAvailability",
                httpMethod = "GET",
                urlMapping = "/user/checkUsernameAvailability",
                protected = false,
                requestBody = "{}",
                requestParam = "{username: " + username + "}")
        )

        val isAvailable = !userRepository.existsByUsername(username)
        return UserIdentityAvailability(isAvailable)
    }

    @GetMapping("/user/checkEmailAvailability")
    fun checkEmailAvailability(@RequestParam(value = "email") email: String): UserIdentityAvailability {

        logRepository.save(Log(
                controller = "UserController",
                methodName = "checkEmailAvailability",
                httpMethod = "GET",
                urlMapping = "/user/checkEmailAvailability",
                protected = false,
                requestBody = "{}",
                requestParam = "{email: " + email + "}")
        )

        val isAvailable = !userRepository.existsByEmail(email)
        return UserIdentityAvailability(isAvailable)
    }

    @GetMapping("/users/{username}")
    fun getUserProfile(@PathVariable(value = "username") username: String): UserProfile {

        logRepository.save(Log(
                controller = "UserController",
                methodName = "getUserProfile",
                httpMethod = "GET",
                urlMapping = "/users/{username}",
                protected = false,
                requestBody = "{}",
                requestParam = "{username: " + username + "}")
        )

        val user = userRepository.findByUsername(username)

        if (user != null) {
            return UserProfile(
                    user.username,
                    user.fullname,
                    user.userInfo.location,
                    Education(
                            user.userInfo.university,
                            user.userInfo.graduation,
                            user.userInfo.major,
                            user.userInfo.degree),
                    user.userInfo.hidden,
                    StrongSkill(
                            user.userInfo.strongSkillName,
                            user.userInfo.strongSkilldescription
                    ),
                    Urls(
                            user.userInfo.github,
                            user.userInfo.linked_in,
                            user.userInfo.web
                    ),
                    user.userInfo.skills,
                    Employment(
                            user.userInfo.company,
                            user.userInfo.currentRole,
                            Reference(
                                    user.userInfo.referenceName,
                                    user.userInfo.referenceNumber
                            )
                    ),
                    user.userInfo.avatar_url,
                    user.userInfo.createdAt
            )
        } else throw ResourceNotFoundException("Profile", "username", username)
    }
}