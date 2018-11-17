package kz.scope. hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.UserInfo
import kz.scope.hiremeserver.payload.*
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
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userInfoRepository: UserInfoRepository

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser currentUser: UserPrincipal): UserSummary {
        return UserSummary(currentUser.id, currentUser.username, currentUser.fullname)
    }


    //find the user in the DB and set new value to its userInfo
    @PostMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun postCurrentUserProfile(@CurrentUser currentUser: UserPrincipal, @RequestBody userProfile: UserProfile): ResponseEntity<*> {

        val user = userRepository.findByUsername(userProfile.username)

        if (user == null){
            return ResponseEntity(ApiResponse(false, "No such user"), HttpStatus.EXPECTATION_FAILED)
        } else if (!user.username.equals(currentUser.username)){
            return ResponseEntity(ApiResponse(false, "You cannot edit user information for other user"), HttpStatus.EXPECTATION_FAILED)
        } else {

            user.userInfo.location = userProfile.location
            user.userInfo.position = userProfile.employment.position
            user.userInfo.company = userProfile.employment.company
            user.userInfo.currentRole = userProfile.current_role
            user.userInfo.university = userProfile.education.university
            user.userInfo.graduationYear = userProfile.education.graduation_year
            user.userInfo.graduationMonth = userProfile.education.graduation_month
            user.userInfo.major = userProfile.education.major
            user.userInfo.degree = userProfile.education.degree
            user.userInfo.hidden = userProfile.hidden
            user.userInfo.jobType = userProfile.job_type
            user.userInfo.jobField = userProfile.job_field
            user.userInfo.skills = userProfile.skills


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
        val user = userRepository.findByUsername(currentUser.username)
        if (user != null) {
            return UserProfile(
                    user.username,
                    user.fullname,
                    user.userInfo.location,
                    Employment(
                            user.userInfo.position,
                            user.userInfo.company),
                    user.userInfo.currentRole,
                    Education(
                            user.userInfo.university,
                            user.userInfo.graduationYear,
                            user.userInfo.graduationMonth,
                            user.userInfo.major,
                            user.userInfo.degree),
                    user.userInfo.hidden,
                    user.userInfo.jobType,
                    user.userInfo.jobField,
                    user.userInfo.skills)
        } else throw ResourceNotFoundException("Profile", "username", currentUser.username)
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

        if(user != null) {
            return UserProfile(
                    user.username,
                    user.fullname,
                    user.userInfo.location,
                    Employment(
                            user.userInfo.position,
                            user.userInfo.company),
                    user.userInfo.currentRole,
                    Education(
                            user.userInfo.university,
                            user.userInfo.graduationYear,
                            user.userInfo.graduationMonth,
                            user.userInfo.major,
                            user.userInfo.degree),
                    user.userInfo.hidden,
                    user.userInfo.jobType,
                    user.userInfo.jobField,
                    user.userInfo.skills)
        } else throw ResourceNotFoundException("Profile", "username", username)
    }
}