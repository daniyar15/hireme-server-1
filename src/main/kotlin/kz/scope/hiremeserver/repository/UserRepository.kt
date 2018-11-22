package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by scope team on 02/08/17.
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByUsernameOrEmail(username: String, email: String): User?

    fun findByIdIn(userIds: List<Long>): List<User>

    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findByFollowing(following: User): List<User>
}