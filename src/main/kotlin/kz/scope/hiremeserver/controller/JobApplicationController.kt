package kz.scope.hiremeserver.controller

import com.google.gson.Gson
import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.*
import kz.scope.hiremeserver.payload.*
import kz.scope.hiremeserver.repository.*
import kz.scope.hiremeserver.security.CurrentUser
import kz.scope.hiremeserver.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private val logger1 = LoggerFactory.getLogger(CompanyController::class.java)

@RestController
@RequestMapping("/api")
class JobApplicationController {

    @Autowired
    lateinit var logRepository: LogRepository

    var gson = Gson()

    @Autowired
    lateinit var companyRepository: CompanyRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jobOfferRepository: JobOfferRepository

    @Autowired
    lateinit var jobApplicationRepository: JobApplicationRepository

    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    fun apply(@RequestParam(value = "job_offer_id", required = true) jobOfferId: Long,
              @CurrentUser currentUser: UserPrincipal) : ResponseEntity<*> {

        logRepository.save(Log(
                controller = "JobApplicationController",
                methodName = "apply",
                httpMethod = "POST",
                urlMapping = "/apply",
                protected = true,
                requestBody = "{}",
                requestParam = "{job_offer_id: " + jobOfferId + "}")
        )

        // getting current user of class User
        val currentUserId = currentUser.id
        val currentUserOptional = userRepository.findById(currentUserId)
        val currUser: User

        if (currentUserOptional.isPresent) {
            currUser = currentUserOptional.get()
        } else {
            throw ResourceNotFoundException("User", "id", currentUserId)
        }

        val jobOfferOptional = jobOfferRepository.findById(jobOfferId)
        val jobOffer: JobOffer

        if (jobOfferOptional.isPresent) {
            jobOffer = jobOfferOptional.get()
        } else {
            throw ResourceNotFoundException("Job offer", "id", currentUserId)
        }

        val jobApplication = JobOfferApplication(jobOffer, currUser)

        val result = jobApplicationRepository.save(jobApplication)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("{id}")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location).body(ApiResponse(true, "Job application submitted successfully"))
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('USER')")
    fun getMyApplications(@CurrentUser currentUser: UserPrincipal) : List<JobApplicationSummary> {

        logRepository.save(Log(
                controller = "JobApplicationController",
                methodName = "getMyApplications",
                httpMethod = "GET",
                urlMapping = "/my-applications",
                protected = true,
                requestBody = "{}",
                requestParam = "{}")
        )

        // getting current user of class User
        val currentUserId = currentUser.id
        val currentUserOptional = userRepository.findById(currentUserId)
        val currUser: User

        if (currentUserOptional.isPresent) {
            currUser = currentUserOptional.get()
        } else {
            throw ResourceNotFoundException("User", "id", currentUserId)
        }

        val applications: MutableList<JobApplicationSummary> = ArrayList()
        for (application in currUser.jobApplications) {
            val jobOfferSummary = JobOfferSummary(application.jobOffer.id, application.jobOffer.company.name,
                    application.jobOffer.position)
            val userSummary = UserSummary(currentUser.id, currUser.username, currUser.fullname)
            applications.add(JobApplicationSummary(application.id, userSummary, jobOfferSummary, application.createdAt))
        }
        return applications
    }

    @GetMapping("/applications-of-my-companies")
    @PreAuthorize("hasRole('USER')")
    fun getApplicationsOfCompanies(@CurrentUser currentUser: UserPrincipal) : List<ApplicationByCompany> {

        logRepository.save(Log(
                controller = "JobApplicationController",
                methodName = "getApplicationsOfCompanies",
                httpMethod = "GET",
                urlMapping = "/applications-of-my-companies",
                protected = true,
                requestBody = "{}",
                requestParam = "{}")
        )

        // getting current user of class User
        val currentUserId = currentUser.id
        val currentUserOptional = userRepository.findById(currentUserId)
        val currUser: User

        if (currentUserOptional.isPresent) {
            currUser = currentUserOptional.get()
        } else {
            throw ResourceNotFoundException("User", "id", currentUserId)
        }

        val companies: MutableList<Company> = ArrayList()
        for (m in currUser.managing) {
            companies.add(m.company)
        }

        val applicationsByCompany : MutableList<ApplicationByCompany> = ArrayList()
        for (company in companies) {
            val jobApplicationsSummary : MutableList<JobApplicationSummary> = ArrayList()

            for (job_offer in company.job_offers) {

                for (application in job_offer.applications) {
                    val jobOfferSummary = JobOfferSummary(application.jobOffer.id, application.jobOffer.company.name,
                            application.jobOffer.position)
                    val userSummary = UserSummary(application.user.id, application.user.username, application.user.fullname)
                    jobApplicationsSummary.add(JobApplicationSummary(application.id, userSummary, jobOfferSummary, application.createdAt))
                    application.isViewed = true
                    jobApplicationRepository.save(application)
                }
            }
            applicationsByCompany.add(ApplicationByCompany(company.name, jobApplicationsSummary))
        }
        return applicationsByCompany
    }

    @GetMapping("/unviewed-num")
    @PreAuthorize("hasRole('USER')")
    fun getUnviewedNum(@CurrentUser currentUser: UserPrincipal) : Int {

        logRepository.save(Log(
                controller = "JobApplicationController",
                methodName = "getUnviewedNum",
                httpMethod = "GET",
                urlMapping = "/unviewed-num",
                protected = true,
                requestBody = "{}",
                requestParam = "{}")
        )

        // getting current user of class User
        val currentUserId = currentUser.id
        val currentUserOptional = userRepository.findById(currentUserId)
        val currUser: User

        if (currentUserOptional.isPresent) {
            currUser = currentUserOptional.get()
        } else {
            throw ResourceNotFoundException("User", "id", currentUserId)
        }

        val companies: MutableList<Company> = ArrayList()
        for (m in currUser.managing) {
            companies.add(m.company)
        }

        var counter: Int = 0
        for (company in companies) {

            for (job_offer in company.job_offers) {

                for (application in job_offer.applications) {
                    if (application.isViewed.not()) {
                        counter += 1
                    }
                }
            }
        }
        return counter
    }
}