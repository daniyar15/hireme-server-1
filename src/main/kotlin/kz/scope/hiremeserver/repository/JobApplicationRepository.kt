package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.JobOfferApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobApplicationRepository : JpaRepository<JobOfferApplication, Long> {

}