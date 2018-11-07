package kz.scope. hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.UserInfo
import kz.scope.hiremeserver.payload.ApiResponse
import kz.scope.hiremeserver.payload.StudentProfile
import kz.scope.hiremeserver.payload.UserIdentityAvailability
import kz.scope.hiremeserver.payload.UserSummary
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
import java.time.Instant

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
    @PostMapping("/user/me/profile")
    @PreAuthorize("hasRole('USER')")
    fun postCurrentUserProfile(@CurrentUser @RequestBody currentStudent: StudentProfile): ResponseEntity<*> {

        val user = userRepository.findByUsername(currentStudent.username)

        return if (user == null){
            ResponseEntity(ApiResponse(false, "No such user"), HttpStatus.EXPECTATION_FAILED)
        }else {
            user.userInfo = UserInfo(currentStudent)

            userInfoRepository.save(user.userInfo)
            val result = userRepository.save(user)



            val location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/users/{username}")
                    .buildAndExpand(result.username).toUri()

            ResponseEntity.created(location).body(ApiResponse(true, "User profile edited successfully"))
        }
    }


    //find the user in the DB and return its userInfo
    @GetMapping("/user/me/profile")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUserProfile(@CurrentUser currentStudent: UserPrincipal) : StudentProfile{
        val student = userInfoRepository.findByUsername(currentStudent.username)
        if (student != null) {
            return StudentProfile(student.id, student.username, student.fullname, student.email,
                    student.sOrGrad, student.location, student.fields, student.education, student.degree,
                    student.display, student.jobType, student.roleSpecification, student.skillSet)
        }else{

            return StudentProfile(0, "No", "Such", "User",
                    false, "", "", "", "",
                    true, "", "", "")
        }
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
    fun getUserProfile(@PathVariable(value = "username") username: String): StudentProfile {
        val userInfo = userInfoRepository.findByUsername(username)
            ?: throw ResourceNotFoundException("User", "username", username)
        return StudentProfile(userInfo.id, userInfo.username, userInfo.fullname, userInfo.email, userInfo.sOrGrad,
                userInfo.location, userInfo.fields, userInfo.education, userInfo.degree, userInfo.display, userInfo.jobType,
                userInfo.roleSpecification, userInfo.skillSet)
    }
}