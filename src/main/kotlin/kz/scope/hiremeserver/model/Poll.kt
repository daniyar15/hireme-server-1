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

@Entity
@Table(name = "polls")
class Poll : UserDateAudit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotBlank
    @Size(max = 140)
    lateinit var question: String

    @OneToMany(
        mappedBy = "poll",
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER,
        orphanRemoval = true
    )
    @Size(min = 2, max = 6)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    private var choices: MutableList<Choice> = ArrayList()

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
