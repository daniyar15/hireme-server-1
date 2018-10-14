package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.Role
import kz.scope.hiremeserver.model.RoleName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */
@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(roleName: RoleName): Optional<Role>
}
