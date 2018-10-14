package kz.scope.hiremeserver.util

import kz.scope.hiremeserver.model.Poll
import kz.scope.hiremeserver.model.User
import kz.scope.hiremeserver.payload.ChoiceResponse
import kz.scope.hiremeserver.payload.PollResponse
import kz.scope.hiremeserver.payload.UserSummary
import java.time.Instant
import java.util.stream.Collectors

object ModelMapper {

    fun mapPollToPollResponse(poll: Poll, choiceVotesMap: Map<Long, Long>, creator: User, userVote: Long?): PollResponse {
        val pollResponse = PollResponse()
        pollResponse.id = poll.id
        pollResponse.question = poll.question
        pollResponse.creationDateTime = poll.createdAt
        pollResponse.expirationDateTime = poll.expirationDateTime
        val now = Instant.now()
        pollResponse.expired = poll.expirationDateTime.isBefore(now)

        val choiceResponses = poll.choices.stream().map { choice ->
            val choiceResponse = ChoiceResponse()
            choiceResponse.id = choice.id
            choiceResponse.text = choice.text

            if (choiceVotesMap.containsKey(choice.id)) {
                choiceResponse.voteCount = choiceVotesMap[choice.id]!!
            } else {
                choiceResponse.voteCount = 0
            }
            choiceResponse
        }.collect(Collectors.toList())

        pollResponse.choices = choiceResponses
        val creatorSummary = UserSummary(creator.id, creator.username, creator.name)
        pollResponse.createdBy = creatorSummary

        if (userVote != null) {
            pollResponse.selectedChoice = userVote
        }

        val totalVotes = pollResponse.choices.stream().mapToLong { it.voteCount }.sum()
        pollResponse.totalVotes = totalVotes

        return pollResponse
    }

}
