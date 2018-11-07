package kz.scope.hiremeserver.model

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.NaturalId
import javax.persistence.*

/**
 * Created by scope team on 01/08/17.
 */
@Entity
@Table(name = "roles")
class   Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    lateinit var name: RoleName

    // according to https://www.callicoder.com/spring-boot-spring-security-jwt-mysql-react-app-part-3/
    @OneToMany(mappedBy = "job_offer", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    var job_offers: Set<JobOffer> = HashSet()
}
