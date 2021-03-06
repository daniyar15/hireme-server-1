package kz.scope.hiremeserver.security

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by scope team on 02/08/17.
 */

@Service
class CustomUserDetailsService : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(usernameOrEmail: String): UserDetails {
        // Let people login with either username or email
        val user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            ?: throw UsernameNotFoundException("User not found with username or email : $usernameOrEmail")

        return UserPrincipal.create(user)
    }

    @Transactional
    fun loadUserById(id: Long): UserDetails {
        val user = userRepository.findById(id).orElseThrow { ResourceNotFoundException("User", "id", id) }

        return UserPrincipal.create(user)
    }
}