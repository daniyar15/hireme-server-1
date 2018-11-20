package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.Company
import kz.scope.hiremeserver.model.JobOffer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by scope team on 02/08/17.
 */
@Repository
interface JobOfferRepository : JpaRepository<JobOffer, Long> {
    fun findByCompany(company: Company): List<JobOffer>
    fun findByPosition(position: String): List<JobOffer>
}
