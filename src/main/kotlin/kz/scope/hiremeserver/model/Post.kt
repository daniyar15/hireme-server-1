package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "post")
class Post() : DateAudit(), Comparable<Post> {
    constructor(isCompany: Boolean, authorId: Long, title: String, text: String, photoLink: String):this() {
        this.isCompany = isCompany
        this.authorId = authorId
        this.title = title
        this.text = text
        this.photoLink = photoLink
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var isCompany: Boolean = false
    var authorId: Long = 0

    @Size(max = 50)
    lateinit var title: String

    lateinit var text: String

    lateinit var photoLink: String

    @ManyToMany(cascade = [
        CascadeType.PERSIST,
        CascadeType.MERGE
    ])
    @JoinTable(name = "post_job_offer",
            joinColumns = [JoinColumn(name = "post_id")],
            inverseJoinColumns = [JoinColumn(name = "job_offer_id")])
    var jobOffers: MutableSet<JobOffer> = HashSet<JobOffer>()

    override fun compareTo(other: Post): Int {
        val thisYears = this.createdAt.epochSecond
        val otherYears = this.createdAt.epochSecond

        // descending order
        val diffSeconds = (otherYears - thisYears)*31556952

        return diffSeconds.toInt()
    }
}