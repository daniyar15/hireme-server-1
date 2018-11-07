package kz.scope.hiremeserver.repository


import kz.scope.hiremeserver.model.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


/**
 * Created by scope team on 02/08/17.
 */
@Repository
interface UserInfoRepository : JpaRepository<UserInfo, Long> {

    fun findByIdIn(list: List<Long>): List<UserInfo>

    fun existsByIdIn(userIds: List<Long>): Boolean
}