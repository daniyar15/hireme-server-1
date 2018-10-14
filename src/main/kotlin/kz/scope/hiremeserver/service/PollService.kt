package kz.scope.hiremeserver.service

import kz.scope.hiremeserver.exception.BadRequestException
import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.*
import kz.scope.hiremeserver.payload.PagedResponse
import kz.scope.hiremeserver.payload.PollRequest
import kz.scope.hiremeserver.payload.PollResponse
import kz.scope.hiremeserver.payload.VoteRequest
import kz.scope.hiremeserver.repository.PollRepository
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.repository.VoteRepository
import kz.scope.hiremeserver.security.UserPrincipal
import kz.scope.hiremeserver.util.AppConstants
import kz.scope.hiremeserver.util.ModelMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.stream.Collectors

private val logger = LoggerFactory.getLogger(PollService::class.java)

@Service
class PollService {

    @Autowired
    lateinit var pollRepository: PollRepository

    @Autowired
    lateinit var voteRepository: VoteRepository

    @Autowired
    lateinit var userRepository: UserRepository

    fun getAllPolls(currentUser: UserPrincipal, page: Int, size: Int): PagedResponse<PollResponse> {
        validatePageNumberAndSize(page, size)

        // Retrieve Polls
        val pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt")
        val polls = pollRepository.findAll(pageable)

        if (polls.numberOfElements == 0) {
            return PagedResponse(emptyList(), polls.number,
                polls.size, polls.totalElements, polls.totalPages, polls.isLast)
        }

        // Map Polls to PollResponses containing vote counts and poll creator details
        val pollIds = polls.map { it.id }.content
        val choiceVoteCountMap = getChoiceVoteCountMap(pollIds)
        val pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds)
        val creatorMap = getPollCreatorMap(polls.content)

        val pollResponses = polls.map { poll ->
            ModelMapper.mapPollToPollResponse(poll,
                choiceVoteCountMap,
                creatorMap[poll.createdBy]!!,
                pollUserVoteMap?.getOrDefault(poll.id, null))
        }.content

        return PagedResponse(pollResponses, polls.number,
            polls.size, polls.totalElements, polls.totalPages, polls.isLast)
    }

    fun getPollsCreatedBy(username: String, currentUser: UserPrincipal, page: Int, size: Int): PagedResponse<PollResponse> {
        validatePageNumberAndSize(page, size)

        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User", "username", username) }

        // Retrieve all polls created by the given username
        val pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt")
        val polls = pollRepository.findByCreatedBy(user.id, pageable)

        if (polls.numberOfElements == 0) {
            return PagedResponse(emptyList(), polls.number,
                polls.size, polls.totalElements, polls.totalPages, polls.isLast)
        }

        // Map Polls to PollResponses containing vote counts and poll creator details
        val pollIds = polls.map { it.id }.content
        val choiceVoteCountMap = getChoiceVoteCountMap(pollIds)
        val pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds)

        val pollResponses = polls.map { poll ->
            ModelMapper.mapPollToPollResponse(poll,
                choiceVoteCountMap,
                user,
                if (pollUserVoteMap == null) null else pollUserVoteMap.getOrDefault(poll.id, null))
        }.content

        return PagedResponse(pollResponses, polls.number,
            polls.size, polls.totalElements, polls.totalPages, polls.isLast)
    }

    fun getPollsVotedBy(username: String, currentUser: UserPrincipal, page: Int, size: Int): PagedResponse<PollResponse> {
        validatePageNumberAndSize(page, size)

        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User", "username", username) }

        // Retrieve all pollIds in which the given username has voted
        val pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt")
        val userVotedPollIds = voteRepository.findVotedPollIdsByUserId(user.id, pageable)

        if (userVotedPollIds.numberOfElements == 0) {
            return PagedResponse(emptyList(), userVotedPollIds.number,
                userVotedPollIds.size, userVotedPollIds.totalElements,
                userVotedPollIds.totalPages, userVotedPollIds.isLast)
        }

        // Retrieve all poll details from the voted pollIds.
        val pollIds = userVotedPollIds.content

        val sort = Sort(Sort.Direction.DESC, "createdAt")
        val polls = pollRepository.findByIdIn(pollIds, sort)

        // Map Polls to PollResponses containing vote counts and poll creator details
        val choiceVoteCountMap = getChoiceVoteCountMap(pollIds)
        val pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds)
        val creatorMap = getPollCreatorMap(polls)

        val pollResponses = polls.stream().map { poll ->
            ModelMapper.mapPollToPollResponse(poll,
                choiceVoteCountMap,
                creatorMap[poll.createdBy]!!,
                if (pollUserVoteMap == null) null else pollUserVoteMap.getOrDefault(poll.id, null))
        }.collect(Collectors.toList())

        return PagedResponse(pollResponses, userVotedPollIds.number, userVotedPollIds.size, userVotedPollIds.totalElements, userVotedPollIds.totalPages, userVotedPollIds.isLast)
    }


    fun createPoll(pollRequest: PollRequest): Poll {
        val poll = Poll()
        poll.question = pollRequest.question

        pollRequest.choices.forEach { choiceRequest -> poll.addChoice(Choice(choiceRequest.text)) }

        val now = Instant.now()
        val expirationDateTime = now.plus(Duration.ofDays(pollRequest.pollLength.days.toLong()))
            .plus(Duration.ofHours(pollRequest.pollLength.hours.toLong()))

        poll.expirationDateTime = expirationDateTime

        return pollRepository.save(poll)
    }

    fun getPollById(pollId: Long, currentUser: UserPrincipal?): PollResponse {
        val poll = pollRepository.findById(pollId).orElseThrow { ResourceNotFoundException("Poll", "id", pollId) }

        // Retrieve Vote Counts of every choice belonging to the current poll
        val votes = voteRepository.countByPollIdGroupByChoiceId(pollId)

        val choiceVotesMap = votes.stream()
            .collect(Collectors.toMap({ it: ChoiceVoteCount -> it.choiceId }, { it.voteCount }))

        // Retrieve poll creator details
        val creator = userRepository.findById(poll.createdBy)
            .orElseThrow { ResourceNotFoundException("User", "id", poll.createdBy) }

        // Retrieve vote done by logged in user
        var userVote: Vote? = null
        if (currentUser != null) {
            userVote = voteRepository.findByUserIdAndPollId(currentUser.id, pollId)
        }

        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap,
            creator, if (userVote != null) userVote.choice!!.id else null)
    }

    fun castVoteAndGetUpdatedPoll(pollId: Long?, voteRequest: VoteRequest, currentUser: UserPrincipal): PollResponse {
        val poll = pollRepository.findById(pollId!!)
            .orElseThrow { ResourceNotFoundException("Poll", "id", pollId) }

        if (poll.expirationDateTime.isBefore(Instant.now())) {
            throw BadRequestException("Sorry! This Poll has already expired")
        }

        val user = userRepository.getOne(currentUser.id)

        val selectedChoice = poll.choices.stream()
            .filter { choice -> choice.id == voteRequest.choiceId }
            .findFirst()
            .orElseThrow { ResourceNotFoundException("Choice", "id", voteRequest.choiceId) }

        var vote = Vote()
        vote.poll = poll
        vote.user = user
        vote.choice = selectedChoice

        try {
            vote = voteRepository.save(vote)
        } catch (ex: DataIntegrityViolationException) {
            logger.info("User {} has already voted in Poll {}", currentUser.id, pollId)
            throw BadRequestException("Sorry! You have already cast your vote in this poll")
        }

        //-- Vote Saved, Return the updated Poll Response now --

        // Retrieve Vote Counts of every choice belonging to the current poll
        val votes = voteRepository.countByPollIdGroupByChoiceId(pollId)

        val choiceVotesMap = votes.stream()
            .collect(Collectors.toMap({ it: ChoiceVoteCount -> it.choiceId }, { it.voteCount }))

        // Retrieve poll creator details
        val creator = userRepository.findById(poll.createdBy)
            .orElseThrow { ResourceNotFoundException("User", "id", poll.createdBy) }

        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap, creator, vote.choice!!.id)
    }


    private fun validatePageNumberAndSize(page: Int, size: Int) {
        if (page < 0) {
            throw BadRequestException("Page number cannot be less than zero.")
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE)
        }
    }

    private fun getChoiceVoteCountMap(pollIds: List<Long>): Map<Long, Long> {
        // Retrieve Vote Counts of every Choice belonging to the given pollIds
        val votes = voteRepository.countByPollIdInGroupByChoiceId(pollIds)

        return votes.stream()
            .collect(Collectors.toMap({ it: ChoiceVoteCount -> it.choiceId }, { it.voteCount }))
    }

    private fun getPollUserVoteMap(currentUser: UserPrincipal?, pollIds: List<Long>): Map<Long, Long>? {
        // Retrieve Votes done by the logged in user to the given pollIds
        var pollUserVoteMap: Map<Long, Long>? = null
        if (currentUser != null) {
            val userVotes = voteRepository.findByUserIdAndPollIdIn(currentUser.id, pollIds)

            pollUserVoteMap = userVotes.stream()
                .collect(Collectors.toMap({ vote: Vote -> vote.poll!!.id }, { vote -> vote.choice!!.id }))
        }
        return pollUserVoteMap
    }

    fun getPollCreatorMap(polls: List<Poll>): Map<Long, User> {
        // Get Poll Creator details of the given list of polls
        val creatorIds = polls.stream()
            .map { it.createdBy }
            .distinct()
            .collect(Collectors.toList())

        val creators = userRepository.findByIdIn(creatorIds)

        return creators.stream()
            .collect(Collectors.toMap({ it: User -> it.id }, { it }))
    }
}
