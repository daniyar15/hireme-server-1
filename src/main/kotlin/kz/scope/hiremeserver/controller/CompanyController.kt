package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Company
import kz.scope.hiremeserver.payload.CompanyProfile
import kz.scope.hiremeserver.repository.CompanyRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        return CompanyProfile(company.name, company.location)
    }

}