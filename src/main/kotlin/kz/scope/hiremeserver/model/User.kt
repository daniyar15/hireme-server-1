package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import org.hibernate.annotations.NaturalId
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "users", uniqueConstraints = [
    UniqueConstraint(columnNames = ["username"]),
    UniqueConstraint(columnNames = ["email"])
])
public data class User(
    @NotBlank @Size(max = 40)
    var name: String,

    @NotBlank @Size(max = 15)
    var username: String,

    @NaturalId @NotBlank @Size(max = 40) @Email
    var email: String,

    @NotBlank @Size(max = 100)
    var password: String

) : DateAudit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @ManyToMany(fetch = FetchType.LAZY)
    lateinit var roles: Set<Role>
}