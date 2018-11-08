package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.DateAudit
import javax.persistence.*

@Entity
@Table(name = "post")
class Post() : DateAudit() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

}