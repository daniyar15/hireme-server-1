package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import org.hibernate.annotations.NaturalId
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.collections.ArrayList

/**
 * Created by scope team on 01/08/17.
 */

@Entity
@Table(name = "job_offer")
class JobOffer() : DateAudit() {
    constructor(position: String, responsibilities: String, qualifications: String, locations: MutableList<JobOfferLocation>, company: Company) : this() {
        this.responsibilities = responsibilities
        this.qualifications = qualifications
        // right now, String. In next iteration, needs to have a separate model
        this.locations = locations
        this.position = position
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

    @Size(max = 240)
    @Column(name = "responsibilities")
    lateinit var responsibilities: String

    @Size(max = 40)
    @Column(name = "position")
    lateinit var position: String

    lateinit var qualifications: String

    @OneToMany(mappedBy = "jobOffer")
    var locations: MutableList<JobOfferLocation> = ArrayList<JobOfferLocation>()

    @ManyToMany(mappedBy = "jobOffers")
    lateinit var posts: Set<Post>

    @OneToMany(mappedBy = "jobOffer")
    var applications: MutableList<JobOfferApplication> = ArrayList<JobOfferApplication>()
}