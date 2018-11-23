package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.Log
import org.springframework.data.jpa.repository.JpaRepository

interface LogRepository: JpaRepository<Log, Long> {
    fun findByController(controller: String): List<Log>
    fun findByMethodName(methodName: String): List<Log>
    fun findByHttpMethod(httpMethod: String): List<Log>
    fun findByProtected(protected: Boolean): List<Log>
}