package kz.scope.hiremeserver.model

import kz.scope.hiremeserver.model.audit.UserDateAudit
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by rajeevkumarsingh on 20/11/17.
 */
@Entity
@Table(name = "polls")
class Poll : UserDateAudit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0


    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @Size(min = 2, max = 6)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    var choices: MutableList<Choice> = ArrayList()

    @NotBlank
    @Size(max = 140)
    lateinit var question: String

    @NotNull
    lateinit var expirationDateTime: Instant


    fun addChoice(choice: Choice) {
        choices.add(choice)
        choice.poll = this
    }

    fun removeChoice(choice: Choice) {
        choices.remove(choice)
        choice.poll = null
    }
}
