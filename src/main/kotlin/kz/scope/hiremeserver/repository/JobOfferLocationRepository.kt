package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.JobOffer
import kz.scope.hiremeserver.model.JobOfferLocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobOfferLocationRepository : JpaRepository<JobOfferLocation, Long> {
    fun findByLocation(location: String): List<JobOfferLocation>
}