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
 * Created by rajeevkumarsingh on 02/08/17.
 */

@Service
open class CustomUserDetailsService : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(usernameOrEmail: String): UserDetails {
        // Let people login with either username or email
        val user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow { UsernameNotFoundException("User not found with username or email : $usernameOrEmail") }

        return UserPrincipal.create(user)
    }

    @Transactional
    open fun loadUserById(id: Long): UserDetails {
        val user = userRepository.findById(id).orElseThrow { ResourceNotFoundException("User", "id", id) }

        return UserPrincipal.create(user)
    }
}