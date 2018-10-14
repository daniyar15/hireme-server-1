package kz.scope.hiremeserver.model

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by rajeevkumarsingh on 20/11/17.
 */

@Entity
@Table(name = "choices")
class Choice() {
    constructor(text: String) : this() {
        this.text = text
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    var poll: Poll? = null

    @NotBlank
    @Size(max = 40)
    lateinit var text: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val choice = other as Choice
        return id == choice.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
