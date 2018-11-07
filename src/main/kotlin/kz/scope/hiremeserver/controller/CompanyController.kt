package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Company
import kz.scope.hiremeserver.payload.CompanyCreator
import kz.scope.hiremeserver.payload.CompanyProfile
import kz.scope.hiremeserver.payload.CompanyRequest
import kz.scope.hiremeserver.repository.CompanyRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.web.bind.annotation.*
import java.rmi.registry.LocateRegistry
import javax.validation.Valid

private val logger1 = LoggerFactory.getLogger(UserController::class.java)

@RestController
@RequestMapping("/api")
class CompanyController {

    @Autowired
    lateinit var companyRepository: CompanyRepository

    @GetMapping("companies/{id}")
    fun getCompanyProfile(@PathVariable(value = "id") id: Long): CompanyProfile {
        val companyOptional = companyRepository.findById(id)
        val company: Company

        if (companyOptional.isPresent) company = companyOptional.get()
        else throw ResourceNotFoundException("Company", "id", id)

        var companyCreator = CompanyCreator(company.manager.user.fullname, company.manager.manager_role)
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
            var companyCreator = CompanyCreator(company.manager.user.fullname, company.manager.manager_role)
            companyProfiles.add(CompanyProfile(company.id, company.name, companyCreator, company.logo,
                    company.location, company.numEmployees, company.specialization, company.description, company.createdAt))
        }

        return companyProfiles
    }

//    @PostMapping("company")
//    fun createCompany(@Valid @RequestBody companyRequest: CompanyRequest)

}