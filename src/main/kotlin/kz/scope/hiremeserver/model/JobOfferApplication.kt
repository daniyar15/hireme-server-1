package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import javax.persistence.*

@Entity
@Table(name = "job_offer_application", uniqueConstraints = [UniqueConstraint(columnNames = ["job_offer_id"]), UniqueConstraint(columnNames = ["user_id"])])
class JobOfferApplication : DateAudit() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_offer_id")
    lateinit var jobOffer: JobOffer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    lateinit var user: User

}