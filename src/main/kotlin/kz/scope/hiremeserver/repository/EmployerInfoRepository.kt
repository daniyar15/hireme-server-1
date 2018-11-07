package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.EmployerInfo
import kz.scope.hiremeserver.model.Role
import kz.scope.hiremeserver.model.RoleName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmployerInfoRepository : JpaRepository<EmployerInfo, Long> {
}