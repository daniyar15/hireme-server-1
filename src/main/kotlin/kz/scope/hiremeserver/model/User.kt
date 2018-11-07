package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import kz.scope.hiremeserver.payload.StudentProfile
import org.hibernate.annotations.NaturalId
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by scope team on 01/08/17.
 */

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("username")), UniqueConstraint(columnNames = arrayOf("email"))])
open class User() : DateAudit() {
    constructor(fullname: String, username: String, email: String, password: String) : this() {
        this.fullname = fullname
        this.username = username
        this.email = email
        this.password = password
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @NotBlank
    @Size(max = 40)
    lateinit var fullname: String

    @NotBlank
    @Size(max = 15)
    lateinit var username: String

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    lateinit var email: String

    @NotBlank
    @Size(max = 100)
    lateinit var password: String

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userinfo_id")
    lateinit var userInfo: UserInfo

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: Set<Role> = HashSet()

}