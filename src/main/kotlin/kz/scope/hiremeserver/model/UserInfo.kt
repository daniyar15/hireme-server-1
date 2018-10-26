package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import kz.scope.hiremeserver.payload.StudentProfile
import org.hibernate.annotations.NaturalId
import java.time.Instant
import java.util.HashSet
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


/**
 * Created by scope team on 01/08/17.
 */

@Entity
@Table(name = "students", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("username")), UniqueConstraint(columnNames = arrayOf("email"))])
class UserInfo//this.joinedAt = student.joinedAt
(student: StudentProfile) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var username: String = ""

    var fullname: String = ""

    var email: String = ""

    //var joinedAt: Instant

    //if Student - true, if new graduate - false
    var sOrGrad : Boolean = false

    //city name
    var location : String = ""

    //Fields of work (e.g JavaScript, C++)
    var fields : String = ""

    //University, major etc
    var education : String = ""

    //Bachelor, Master or PhD
    var degree : String = ""

    //keep info private - false, public - true
    var display : Boolean = false

    //part-time, full-time etc
    var jobType : String

    //mobile app developer, system level programmer etc
    var roleSpecification : String

    //technical skills
    var skillSet: String

    init {
        this.username= student.username
        this.fullname= student.fullname
        this.email = student.email
        this.sOrGrad = student.sOrGrad
        this.fields = student.fields
        this.location = student.location
        this.education = student.education
        this.skillSet = student.skillSet
        this.display = student.display
        this.roleSpecification = student.roleSpecification
        this.jobType = student.jobType
        this.degree = student.degree
    }


}