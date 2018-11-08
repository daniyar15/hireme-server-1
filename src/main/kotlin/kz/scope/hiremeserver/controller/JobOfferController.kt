package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Company
import kz.scope.hiremeserver.model.JobOffer
import kz.scope.hiremeserver.payload.JobOfferResponse
import kz.scope.hiremeserver.repository.CompanyRepository
import kz.scope.hiremeserver.repository.JobOfferRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

private val logger1 = LoggerFactory.getLogger(JobOfferController::class.java)

@RestController
@RequestMapping("/api")
class JobOfferController {

    @Autowired
    lateinit var jobOfferRepository: JobOfferRepository

    @Autowired
    lateinit var companyRepository: CompanyRepository

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

    @GetMapping("/job-offer/find-by-company")
    @PreAuthorize("hasRole('USER')")
    fun getJobOfferByCompany(@RequestParam(value = "company_id", required = true) company_id: Long)
            : List<JobOfferResponse> {

        val companyOptional = companyRepository.findById(company_id)
        val company: Company

        if (companyOptional.isPresent) {
            company = companyOptional.get()
        } else {
            throw ResourceNotFoundException("Company", "company_id", company_id)
        }

        val jobOffers = jobOfferRepository.findByCompany(company)
        val jobOfferResponses: MutableList<JobOfferResponse>
        jobOfferResponses = ArrayList<JobOfferResponse>()

        for (jobOffer in jobOffers) {
            jobOfferResponses.add(JobOfferResponse(jobOffer.id, jobOffer.descriptionOfResponsibilities, jobOffer.skills,
                    jobOffer.role, jobOffer.company.id, jobOffer.jobType, jobOffer.createdAt, jobOffer.updatedAt))
        }

        return jobOfferResponses
    }

}
