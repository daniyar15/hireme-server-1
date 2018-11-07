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
@Table(name = "userInfo", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("username")), UniqueConstraint(columnNames = arrayOf("email"))])
class UserInfo(student: StudentProfile) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var username: String = ""

    var fullname: String = ""

    //city name
    var location : String = ""

    var employment: Employment

    var current_role: String = ""

    //University, major etc
    var education : Education

    //keep info private - false, public - true
    var hidden : Boolean = false

    //part-time, full-time etc
    var job_type : String

    var job_field : String

    //technical skills
    var skills: String



    init {
        this.username= student.username
        this.fullname= student.fullname
        this.location = student.location
        this.employment = student.employment
        this.education = student.education
        this.skills = student.skills
        this.hidden = student.hidden
        this.current_role = student.current_role
        this.job_type = student.job_type
        this.job_field = student.job_field
    }


}