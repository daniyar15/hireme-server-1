package kz.scope.hiremeserver.payload

import com.fasterxml.jackson.annotation.JsonInclude

import java.time.Instant

class PollResponse {
    var id: Long = 0
    lateinit var question: String
    lateinit var choices: List<ChoiceResponse>
    lateinit var createdBy: UserSummary
    lateinit var creationDateTime: Instant
    lateinit var expirationDateTime: Instant
    var expired: Boolean = true

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var selectedChoice: Long = 0
    var totalVotes: Long = 0
}
