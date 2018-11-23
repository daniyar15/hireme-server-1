package kz.scope.hiremeserver.model

import com.sun.org.apache.xpath.internal.operations.Bool
import kz.scope.hiremeserver.model.audit.DateAudit
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


/**
 * Created by scope team on 01/08/17.
 */

@Entity
@Table(name = "userInfo", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("id"))])
class UserInfo(): DateAudit() {
    constructor(location: String,
                university: String,
                graduation: String,
                major: String,
                degree: String,
                hidden : Boolean,
                strongSkillName: String,
                strongSkilldescription:String,
                github:String,
                linked_in:String,
                web:String,
                company:String,
                currentRole:String,
                referenceName: String,
                referenceNumber: String,
                skills:String,
                avatar_url: String,
                createdAt : Instant) : this() {
        this.location = location
        this.university = university
        this.graduation = graduation
        this.major = major
        this.degree = degree
        this.hidden = hidden
        this.strongSkillName = strongSkillName
        this.strongSkilldescription = strongSkilldescription
        this.github = github
        this.linked_in = linked_in
        this.web = web
        this.company = company
        this.currentRole = currentRole
        this.referenceName = referenceName
        this.referenceNumber = referenceNumber
        this.skills = skills
        this.avatar_url = avatar_url
        this.createdAt = createdAt
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @Size(max = 40)
    lateinit var location: String
    //education information
    @Size(max = 40)
    lateinit var university: String
    @Column(name = "graduation_date")
    var graduation: String = ""
    @Size(max = 40)
    lateinit var major: String
    @Size(max = 40)
    lateinit var degree: String



    var hidden: Boolean = false
    //strongest skill data
    lateinit var strongSkillName: String
    lateinit var strongSkilldescription: String

    var github : String = ""
    var linked_in : String = ""
    var web : String = ""


    @Size(max = 40)
    lateinit var company: String
    @Size(max = 40)
    @Column(name = "current_role")
    lateinit var currentRole: String
    var referenceName : String = ""
    var referenceNumber : String = ""

    @Size(max = 40)
    lateinit var skills: String

    @OneToOne(mappedBy = "userInfo")
    lateinit var user: User

    lateinit var avatar_url: String
}