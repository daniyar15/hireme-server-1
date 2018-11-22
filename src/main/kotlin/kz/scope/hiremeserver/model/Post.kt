package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import org.springframework.boot.autoconfigure.batch.BatchProperties
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "post")
class Post() : DateAudit(), Comparable<Post> {
    constructor(isCompany: Boolean, authorId: Long, title: String, text: String, photoLink: String, jobOffer: JobOffer):this() {
        this.isCompany = isCompany
        this.authorId = authorId
        this.title = title
        this.text = text
        this.photoLink = photoLink
        this.jobOffer = jobOffer
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var isCompany: Boolean = false
    var authorId: Long = 0

    @Size(max = 50)
    lateinit var title: String

    @Size(max = 140)
    lateinit var text: String

    lateinit var photoLink: String

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "job_offer_id")
    var jobOffer: JobOffer? = null

    override fun compareTo(other: Post): Int {
        val thisYears = this.createdAt.epochSecond
        val otherYears = this.createdAt.epochSecond

        // descending order
        val diffSeconds = (otherYears - thisYears)*31556952

        return diffSeconds.toInt()
    }
}