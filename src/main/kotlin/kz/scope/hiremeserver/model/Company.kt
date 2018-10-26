package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.EmployerInfo
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name="company")
class Company() {
    constructor(manager: EmployerInfo, name: String, location: String) : this() {
        this.manager = manager
        this.name = name
        this.location = name
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotBlank
    @Size(max = 40)
    lateinit var name: String

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_info_id")
    lateinit var manager: EmployerInfo

    @NotBlank
    @Size(max = 100)
    lateinit var location: String
}