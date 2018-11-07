package kz.scope.hiremeserver.repository


import kz.scope.hiremeserver.model.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


/**
 * Created by scope team on 02/08/17.
 */
@Repository
interface UserInfoRepository : JpaRepository<UserInfo, Long> {
    fun findByEmail(email: String): UserInfo?

    fun findByUsernameOrEmail(username: String, email: String): UserInfo?

    fun findByIdIn(userIds: List<Long>): List<UserInfo>

    fun findByUsername(username: String): UserInfo?

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
