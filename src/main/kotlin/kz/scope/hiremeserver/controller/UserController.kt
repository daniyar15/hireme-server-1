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
import java.time.Instant
import java.util.*

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
    protected fun getCurrentUser(@CurrentUser currentUser: UserPrincipal): UserSummary {
        return UserSummary(currentUser.id, currentUser.username, currentUser.fullname)
    }


    //find the user in the DB and set new value to its userInfo
    @PostMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    protected fun postCurrentUserProfile(@CurrentUser @RequestBody currentStudent: StudentProfile): ResponseEntity<*> {

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
        val student = userRepository.findByUsername(currentStudent.username)
        if (student != null) {
            return StudentProfile(
                    student.username,
                    student.fullname,
                    student.userInfo.location,
                    Employment(
                            student.userInfo.position,
                            student.userInfo.company),
                    student.userInfo.current_role,
                    Education(
                            student.userInfo.university,
                            student.userInfo.graduationYear,
                            student.userInfo.graduationMonth,
                            student.userInfo.major,
                            student.userInfo.degree),
                    student.userInfo.hidden,
                    student.userInfo.job_type,
                    student.userInfo.job_field,
                    student.userInfo.skills)
            return StudentProfile("No", "", "userInfo", Employment("", ""),
                    "", Education("", "", "", "", ""),
                    false, "", "", "")
        }else throw ResourceNotFoundException("Profile", "username", currentStudent.username)
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
        val student = userRepository.findByUsername(username)

        if(student != null) {
            return StudentProfile(
                    student.username,
                    student.fullname,
                    student.userInfo.location,
                    Employment(
                            student.userInfo.position,
                            student.userInfo.company),
                    student.userInfo.current_role,
                    Education(
                            student.userInfo.university,
                            student.userInfo.graduationYear,
                            student.userInfo.graduationMonth,
                            student.userInfo.major,
                            student.userInfo.degree),
                    student.userInfo.hidden,
                    student.userInfo.job_type,
                    student.userInfo.job_field,
                    student.userInfo.skills)
        }else throw ResourceNotFoundException("Profile", "username", username)
    }
}