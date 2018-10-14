package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import org.hibernate.annotations.NaturalId
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by rajeevkumarsingh on 01/08/17.
 */

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("username")), UniqueConstraint(columnNames = arrayOf("email"))])
class User() : DateAudit() {
    constructor(name: String, username: String, email: String, password: String) : this() {
        this.name = name
        this.username = username
        this.email = email
        this.password = password
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotBlank
    @Size(max = 40)
    var name: String? = null

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: Set<Role> = HashSet()

}