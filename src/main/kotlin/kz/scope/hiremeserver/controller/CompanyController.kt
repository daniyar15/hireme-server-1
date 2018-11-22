package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Company
import kz.scope.hiremeserver.model.EmployerInfo
import kz.scope.hiremeserver.model.User
import kz.scope.hiremeserver.payload.*
import kz.scope.hiremeserver.repository.CompanyRepository
import kz.scope.hiremeserver.repository.EmployerInfoRepository
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.security.CurrentUser
import kz.scope.hiremeserver.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.rmi.registry.LocateRegistry
import javax.validation.Valid

private val logger1 = LoggerFactory.getLogger(CompanyController::class.java)

@RestController
@RequestMapping("/api")
class CompanyController {

    @Autowired
    lateinit var companyRepository: CompanyRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var employerInfoRepository: EmployerInfoRepository

    @GetMapping("companies/{id}")
    fun getCompanyProfile(@PathVariable(value = "id") id: Long): CompanyProfile {
        val companyOptional = companyRepository.findById(id)
        val company: Company

        if (companyOptional.isPresent) company = companyOptional.get()
        else throw ResourceNotFoundException("Company", "id", id)

        var companyCreator = CompanyCreator(company.manager.user.username, company.manager.manager_role)
        return CompanyProfile(company.id, company.name, companyCreator, company.logo,
                company.location, company.numEmployees, company.specialization, company.description, company.createdAt)
    }

    @GetMapping("companies/find")
    fun getCompanyProfile(@RequestParam(value = "name", required = true) name: String,
                          @RequestParam(value = "location", required = true) location: String): List<CompanyProfile> {
        val companyList = companyRepository.findByNameAndLocation(name, location)

        if (companyList.isEmpty()) throw ResourceNotFoundException("Company", "name", name)

        val companyProfiles: MutableList<CompanyProfile> = ArrayList<CompanyProfile>()
        for (company in companyList) {
            var companyCreator = CompanyCreator(company.manager.user.username, company.manager.manager_role)
            companyProfiles.add(CompanyProfile(company.id, company.name, companyCreator, company.logo,
                    company.location, company.numEmployees, company.specialization, company.description, company.createdAt))
        }

        return companyProfiles
    }

    @PostMapping("company")
    @PreAuthorize("hasRole('USER')")
    fun createCompany(@CurrentUser currentUser: UserPrincipal, @Valid @RequestBody companyRequest: CompanyRequest) : ResponseEntity<*> {
        val user = userRepository.findByUsername(companyRequest.creator.username)

        if (user == null) {
            return ResponseEntity(ApiResponse(false, "Such creator does not exists"), HttpStatus.EXPECTATION_FAILED)
        } else if (user.id != currentUser.id) {
            return ResponseEntity(ApiResponse(false, "You can only register a company on your account."), HttpStatus.UNAUTHORIZED)
        }

        val manager = EmployerInfo(user, companyRequest.creator.role)
        user.managing.add(manager)

        val company = Company(manager, companyRequest.name, companyRequest.location, companyRequest.logo_url,
                companyRequest.employee_number, companyRequest.specialization, companyRequest.description)

        employerInfoRepository.save(manager)
        val result = companyRepository.save(company)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/companies/{id}")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location).body(ApiResponse(true, "Company registered successfully"))
    }

    @GetMapping("/my-companies")
    @PreAuthorize("hasRole('USER')")
    fun getMyCompanies(@CurrentUser currentUser: UserPrincipal) : List<CompanySummary> {
        val userOptional = userRepository.findById(currentUser.id)
        val user: User
        val companies: MutableList<CompanySummary> = ArrayList()

        if (userOptional.isPresent) {
            user = userOptional.get()
        } else {
            throw ResourceNotFoundException("User", "id", currentUser.id)
        }

        for (employer in user.managing) {
            companies.add(CompanySummary(
                    id = employer.company.id,
                    name = employer.company.name,
                    description = employer.company.description))
        }
        return companies
    }

}