package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import org.hibernate.annotations.NaturalId
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by scope team on 01/08/17.
 */

@Entity
@Table(name = "job_offer")
class JobOffer() : DateAudit() {
    constructor(descriptionOfResponsibilities: String, jobType: String, skills: String, role: String, company: Company) : this() {
        this.descriptionOfResponsibilities = descriptionOfResponsibilities
        this.jobType = jobType
        // right now, String. In next iteration, needs to have a separate model
        this.skills = skills
        this.role = role
        this.company = company
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    // according to https://www.callicoder.com/spring-boot-spring-security-jwt-mysql-react-app-part-3/
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull
    lateinit var company: Company

    @NotBlank
    @Size(max = 200)
    @Column(name = "description_of_responsibilities")
    lateinit var descriptionOfResponsibilities: String

    @NotBlank
    @Size(max = 40)
    @Column(name = "job_type")
    lateinit var jobType: String

    @NotBlank
    @Size(max = 100)
    lateinit var skills: String

    @NotBlank
    @Size(max = 40)
    lateinit var role: String

    @ManyToMany(mappedBy = "jobOffers")
    lateinit var posts: Set<Post>
}