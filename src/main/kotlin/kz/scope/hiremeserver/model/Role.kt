package kz.scope.hiremeserver.model

import org.hibernate.annotations.NaturalId
import javax.persistence.*

/**
 * Created by scope team on 01/08/17.
 */
@Entity
@Table(name = "roles")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    lateinit var name: RoleName
}
