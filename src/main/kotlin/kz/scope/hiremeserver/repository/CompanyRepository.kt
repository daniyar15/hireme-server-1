package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
    fun findByNameAndLocation(name: String, location: String): List<Company>
}