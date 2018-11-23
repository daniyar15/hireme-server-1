package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.repository.LogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class AdminController {
    @Autowired
    lateinit var logRepository: LogRepository

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    fun getLogs() : List<String> {
        val topHundred: PageRequest = PageRequest.of(0, 100)
        val logs = logRepository.findAll(topHundred)

        val result: MutableList<String> = ArrayList()

        for (log in logs) {
            result.add(log.toString())
        }

        return result
    }

    @GetMapping("/logs-by-controller")
    @PreAuthorize("hasRole('ADMIN')")
    fun getLogsByController(@RequestParam(value = "name") controllerName: String) : List<String> {
        val allLogs = logRepository.findByController(controllerName)

        val result: MutableList<String> = ArrayList()

        for (i in 0..99) {
            if (i == allLogs.size) {
                break
            }
            result.add(allLogs[i].toString())
        }

        return result
    }

    @GetMapping("/logs-by-http-method")
    @PreAuthorize("hasRole('ADMIN')")
    fun getLogsByHttpMethod(@RequestParam(value = "name") httpMethodName: String) : List<String> {
        val allLogs = logRepository.findByHttpMethod(httpMethodName)

        val result: MutableList<String> = ArrayList()

        for (i in 0..99) {
            if (i == allLogs.size) {
                break
            }
            result.add(allLogs[i].toString())
        }

        return result
    }

    @GetMapping("/logs-by-protected")
    @PreAuthorize("hasRole('ADMIN')")
    fun getLogsByHttpMethod(@RequestParam(value = "is_protected") protected: Boolean) : List<String> {
        val allLogs = logRepository.findByProtected(protected)

        val result: MutableList<String> = ArrayList()

        for (i in 0..99) {
            if (i == allLogs.size) {
                break
            }
            result.add(allLogs[i].toString())
        }

        return result
    }

}