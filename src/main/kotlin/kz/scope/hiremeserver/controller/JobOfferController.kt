package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.model.JobOffer
import kz.scope.hiremeserver.repository.JobOfferRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
    fun getJobOffer(@PathVariable(value = "id") id: Long):

}
