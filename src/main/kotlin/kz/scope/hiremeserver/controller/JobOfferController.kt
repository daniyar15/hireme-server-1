package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Company
import kz.scope.hiremeserver.model.JobOffer
import kz.scope.hiremeserver.model.JobOfferLocation
import kz.scope.hiremeserver.model.User
import kz.scope.hiremeserver.payload.*
import kz.scope.hiremeserver.repository.CompanyRepository
import kz.scope.hiremeserver.repository.JobOfferLocationRepository
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

    @Autowired
    lateinit var jobofferLocationRepository: JobOfferLocationRepository

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

        val company = CompanyJobOfferResponse(jobOffer.company.id, jobOffer.company.name, jobOffer.company.logo)
        val locations: MutableList<String> = ArrayList<String>()
        for (location in jobOffer.locations) {
            locations.add(location.location)
        }
        return JobOfferResponse(jobOffer.id, company, jobOffer.position,
                jobOffer.responsibilities, jobOffer.qualifications, locations, jobOffer.createdAt, jobOffer.updatedAt)
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

        val companyResponse = CompanyJobOfferResponse(company.id, company.name, company.logo)
        for (jobOffer in jobOffers) {
            val locations: MutableList<String> = ArrayList<String>()
            for (location in jobOffer.locations) {
                locations.add(location.location)
            }

            jobOfferResponses.add(JobOfferResponse(jobOffer.id, companyResponse, jobOffer.position,
                    jobOffer.responsibilities, jobOffer.qualifications, locations, jobOffer.createdAt,
                    jobOffer.updatedAt))
        }

        return jobOfferResponses
    }

    @GetMapping("/job-offers/find-by-position")
    @PreAuthorize("hasRole('USER')")
    fun getJobOfferByPosition(@RequestParam(value = "position", required = true) position: String)
            : List<JobOfferResponse> {

        val jobOffers = jobOfferRepository.findByPosition(position)
        val jobOfferResponses: MutableList<JobOfferResponse> = ArrayList<JobOfferResponse>()

        for (jobOffer in jobOffers) {
            val companyResponse = CompanyJobOfferResponse(jobOffer.company.id, jobOffer.company.name, jobOffer.company.logo)
            val locations: MutableList<String> = ArrayList<String>()
            for (location in jobOffer.locations) {
                locations.add(location.location)
            }

            jobOfferResponses.add(JobOfferResponse(jobOffer.id, companyResponse, jobOffer.position,
                    jobOffer.responsibilities, jobOffer.qualifications, locations, jobOffer.createdAt,
                    jobOffer.updatedAt))
        }
        return jobOfferResponses
    }

    @GetMapping("/job-offers/find-by-location")
    @PreAuthorize("hasRole('USER')")
    fun getJobOfferByLocation(@RequestParam(value = "location", required = true) locationToSearch: String)
            : List<JobOfferResponse> {

        val jobOfferLocations = jobofferLocationRepository.findByLocation(locationToSearch)
        val jobOfferResponses: MutableList<JobOfferResponse> = ArrayList<JobOfferResponse>()
        for (jobOfferLocation in jobOfferLocations) {
            val jobOffer = jobOfferLocation.jobOffer
            val companyResponse = CompanyJobOfferResponse(jobOffer.company.id, jobOffer.company.name, jobOffer.company.logo)
            val locations: MutableList<String> = ArrayList<String>()
            for (location in jobOffer.locations) {
                locations.add(location.location)
            }

            jobOfferResponses.add(JobOfferResponse(jobOffer.id, companyResponse, jobOffer.position,
                    jobOffer.responsibilities, jobOffer.qualifications, locations, jobOffer.createdAt,
                    jobOffer.updatedAt))
        }

        return jobOfferResponses
    }

    @PostMapping("/job-offer")
    @PreAuthorize("hasRole('USER')")
    fun createJobOffer(@CurrentUser currentUser: UserPrincipal, @Valid @RequestBody jobOfferRequest: JobOfferRequest) : ResponseEntity<*> {
        val companyOptional = companyRepository.findById(jobOfferRequest.company.company_id)
        val company: Company

        if (companyOptional.isPresent) company = companyOptional.get()
        else return ResponseEntity(ApiResponse(false, "Such company does not exists"), HttpStatus.EXPECTATION_FAILED)

        // getting current user of class User
        val currentUserId = currentUser.id
        val currentUserOptional = userRepository.findById(currentUserId)
        val curUser: User

        if (currentUserOptional.isPresent) {
            curUser = currentUserOptional.get()
        } else {
            throw ResourceNotFoundException("User", "id", currentUserId)
        }

        // getting all companies associated with the current users
        val employers = curUser.managing

        val companies: MutableList<Company> = ArrayList<Company>()

        for (employer in employers) {
            companies.add(employer.company)
        }

        var count = 0
        for (company_candidate in companies) {
            if (company_candidate.id == jobOfferRequest.company.company_id) {
                count+=1
            }
        }

        if (count == 0) {
            return ResponseEntity(ApiResponse(false, "You can only post a job offer for a company managed by your account."), HttpStatus.UNAUTHORIZED)
        }


        val jobOffer = JobOffer(jobOfferRequest.position, jobOfferRequest.responsibilities, jobOfferRequest.qualifications,
                ArrayList<JobOfferLocation>(), company)

        for (locationName in jobOfferRequest.locations) {
            val newLocation = JobOfferLocation(locationName)
            newLocation.jobOffer = jobOffer
            jobOffer.locations.add(newLocation)
        }

        val result = jobOfferRepository.save(jobOffer)
        for (locationToSave in jobOffer.locations) {
            jobofferLocationRepository.save(locationToSave)
        }
        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/job-offers/{id}")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location).body(ApiResponse(true, "Job offer registered successfully"))
    }

}
