package kz.scope.hiremeserver.model

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "job_offer_location")
class JobOfferLocation() {
    constructor(location: String) : this() {
        this.location = location
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotBlank
    @Size(max = 200)
    lateinit var location: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_offer_id")
    lateinit var jobOffer: JobOffer
}