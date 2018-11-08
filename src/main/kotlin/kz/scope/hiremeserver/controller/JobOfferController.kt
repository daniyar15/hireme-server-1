package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.JobOffer
import kz.scope.hiremeserver.payload.JobOfferResponse
import kz.scope.hiremeserver.repository.JobOfferRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger1 = LoggerFactory.getLogger(JobOfferController::class.java)

@RestController
@RequestMapping("/api")
class JobOfferController {

    @Autowired
    lateinit var jobOfferRepository: JobOfferRepository

    @GetMapping("/job-offers/{id}")
    @PreAuthorize("hasRole('USER')")
    fun getJobOffer(@PathVariable(value = "id") id: Long): JobOfferResponse {
        val jobOfferOptional = jobOfferRepository.findById(id)
        val jobOffer: JobOffer

        if (jobOfferOptional.isPresent){
            jobOffer = jobOfferOptional.get()
        } else {
            throw ResourceNotFoundException("Job Offer", "id", id)
        }

        return JobOfferResponse(jobOffer.id, jobOffer.descriptionOfResponsibilities, jobOffer.skills,
                jobOffer.role, jobOffer.company.id, jobOffer.jobType, jobOffer.createdAt, jobOffer.updatedAt)
    }

}
