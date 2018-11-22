package kz.scope.hiremeserver.model

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "employer_info")
class EmployerInfo() {
    constructor(user: User, manager_role: String) : this() {
        this.user = user
        this.manager_role = manager_role
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotBlank
    @Size(max = 40)
    lateinit var  manager_role: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    lateinit var user: User

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "manager")
    lateinit var company: Company
}