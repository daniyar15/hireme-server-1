package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import javax.persistence.*

@Entity
@Table(name = "log")
class Log() : DateAudit() {
    constructor(controller: String, methodName: String, httpMethod: String, urlMapping: String, protected: Boolean, requestParam: String, requestBody:String) : this() {
        this.controller = controller
        this.methodName = methodName
        this.httpMethod = httpMethod
        this.urlMapping = urlMapping
        this.protected = protected
        this.requestBody = requestBody
        this.requestParam = requestParam
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    lateinit var controller: String

    lateinit var methodName: String

    lateinit var httpMethod: String

    lateinit var urlMapping: String

    var protected: Boolean = false

    lateinit var requestParam: String

    lateinit var requestBody: String

    override fun toString(): String {
        return this.id.toString() + " " + this.controller + " " + this.methodName + " " + this.httpMethod + " " +
                this.urlMapping + " " + this.protected.toString() + " " +  this.requestParam + " " + this.requestBody + " " + this.createdAt
    }
}