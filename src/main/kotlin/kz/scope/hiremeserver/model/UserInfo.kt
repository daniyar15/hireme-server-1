package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import kz.scope.hiremeserver.payload.Education
import kz.scope.hiremeserver.payload.Employment
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
@Table(name = "userInfo", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("id"))])
class UserInfo(student: StudentProfile) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var location : String = ""

    var position : String = ""
    var company : String = ""

    var current_role: String = ""

    var university : String = ""
    var graduationYear : String = ""
    var graduationMonth : String = ""
    var major : String = ""
    var degree : String = ""

    //keep info private - false, public - true
    var hidden : Boolean = false

    //part-time, full-time etc
    var job_type : String

    var job_field : String

    //technical skills
    var skills: String



    init {
        this.location = student.location
        this.position = student.employment.position
        this.company = student.employment.company
        this.university = student.education.university
        this.graduationYear = student.education.graduation_year
        this.graduationMonth = student.education.graduation_month
        this.major = student.education.major
        this.degree = student.education.degree
        this.skills = student.skills
        this.hidden = student.hidden
        this.current_role = student.current_role
        this.job_type = student.job_type
        this.job_field = student.job_field
    }


}