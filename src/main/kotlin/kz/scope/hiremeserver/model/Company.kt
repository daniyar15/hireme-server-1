package kz.scope.hiremeserver.model

import jdk.nashorn.internal.codegen.ApplySpecialization
import kz.scope.hiremeserver.model.EmployerInfo
import kz.scope.hiremeserver.model.audit.DateAudit
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name="company")
class Company() : DateAudit() {
    constructor(manager: EmployerInfo, name: String, location: String,
                logo: String, numEmployees: Int, specialization: String, description: String) : this() {
        this.manager = manager
        this.name = name
        this.location = location
        this.logo = logo
        this.numEmployees = numEmployees
        this.specialization = specialization
        this.description = description
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotBlank
    @Size(max = 40)
    lateinit var name: String

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_info_id")
    lateinit var manager: EmployerInfo

    @NotBlank
    @Size(max = 100)
    lateinit var location: String

    lateinit var logo: String

    var numEmployees: Int = 0
    lateinit var specialization: String
    lateinit var description: String

    // according to https://www.callicoder.com/spring-boot-spring-security-jwt-mysql-react-app-part-3/
    @OneToMany(mappedBy = "company")
    var job_offers: Set<JobOffer> = HashSet<JobOffer>()

    @ManyToMany(mappedBy = "followingCompanies")
    lateinit var followers: MutableSet<User>
}
