package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Company
import kz.scope.hiremeserver.model.JobOffer
import kz.scope.hiremeserver.model.User
import kz.scope.hiremeserver.payload.ApiResponse
import kz.scope.hiremeserver.payload.CompanyRequest
import kz.scope.hiremeserver.payload.JobOfferRequest
import kz.scope.hiremeserver.payload.JobOfferResponse
import kz.scope.hiremeserver.repository.CompanyRepository
import kz.scope.hiremeserver.repository.JobOfferRepository
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.security.CurrentUser
import kz.scope.hiremeserver.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

private val logger1 = LoggerFactory.getLogger(JobOfferController::class.java)

@RestController
@RequestMapping("/api")
class JobOfferController {

    @Autowired
    lateinit var jobOfferRepository: JobOfferRepository

    @Autowired
    lateinit var companyRepository: CompanyRepository

    @Autowired
    lateinit var userRepository: UserRepository

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

    @GetMapping("/job-offers/find-by-company")
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
        val jobOfferResponses: MutableList<JobOfferResponse> = ArrayList<JobOfferResponse>()

        for (jobOffer in jobOffers) {
            jobOfferResponses.add(JobOfferResponse(jobOffer.id, jobOffer.descriptionOfResponsibilities, jobOffer.skills,
                    jobOffer.role, jobOffer.company.id, jobOffer.jobType, jobOffer.createdAt, jobOffer.updatedAt))
        }

        return jobOfferResponses
    }

    @GetMapping("/job-offers/find-by-role")
    @PreAuthorize("hasRole('USER')")
    fun getJobOfferByRole(@RequestParam(value = "role", required = true) role: String)
            : List<JobOfferResponse> {

        val jobOffers = jobOfferRepository.findByRole(role)
        val jobOfferResponses: MutableList<JobOfferResponse> = ArrayList<JobOfferResponse>()

        for (jobOffer in jobOffers) {
            jobOfferResponses.add(JobOfferResponse(jobOffer.id, jobOffer.descriptionOfResponsibilities, jobOffer.skills,
                    jobOffer.role, jobOffer.company.id, jobOffer.jobType, jobOffer.createdAt, jobOffer.updatedAt))
        }

        return jobOfferResponses
    }

    // does not find when + symbol is used in skill
    @GetMapping("/job-offers/find-by-skills")
    @PreAuthorize("hasRole('USER')")
    fun getJobOfferBySkills(@RequestParam(value = "skills", required = true) skills: String)
            : List<JobOfferResponse> {

        val jobOffers = jobOfferRepository.findBySkills(skills)
        val jobOfferResponses: MutableList<JobOfferResponse> = ArrayList<JobOfferResponse>()

        for (jobOffer in jobOffers) {
            jobOfferResponses.add(JobOfferResponse(jobOffer.id, jobOffer.descriptionOfResponsibilities, jobOffer.skills,
                    jobOffer.role, jobOffer.company.id, jobOffer.jobType, jobOffer.createdAt, jobOffer.updatedAt))
        }

        return jobOfferResponses
    }

    @PostMapping("/job-offer")
    @PreAuthorize("hasRole('USER')")
    fun createJobOffer(@CurrentUser currentUser: UserPrincipal, @Valid @RequestBody jobOfferRequest: JobOfferRequest) : ResponseEntity<*> {
        val companyOptional = companyRepository.findById(jobOfferRequest.company_id)
        val company: Company

        if (companyOptional.isPresent) company = companyOptional.get()
        else return ResponseEntity(ApiResponse(false, "Such company does not exists"), HttpStatus.EXPECTATION_FAILED)

        // getting current user of class User
        val current_user_id = currentUser.id
        val current_user_optional = userRepository.findById(current_user_id)
        val current_user: User

        if (current_user_optional.isPresent) {
            current_user = current_user_optional.get()
        } else {
            throw ResourceNotFoundException("User", "id", current_user_id)
        }

        // getting all companies associated with the current users
        val employers = current_user.managing

        val companies: MutableList<Company> = ArrayList<Company>()

        for (employer in employers) {
            companies.add(employer.company)
        }

        var count = 0
        for (company_candidate in companies) {
            if (company_candidate.id == jobOfferRequest.company_id) {
                count+=1
            }
        }

        if (count == 0) {
            return ResponseEntity(ApiResponse(false, "You can only post a job offer for a company managed by your account."), HttpStatus.UNAUTHORIZED)
        }

        val jobOffer = JobOffer(jobOfferRequest.description_of_responsibilities, jobOfferRequest.job_type,
                jobOfferRequest.skills, jobOfferRequest.role, company)

        val result = jobOfferRepository.save(jobOffer)
        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/job-offers/{id}")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location).body(ApiResponse(true, "Company registered successfully"))
    }

}
