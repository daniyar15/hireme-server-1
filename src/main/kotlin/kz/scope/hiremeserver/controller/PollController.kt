package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.payload.*
import kz.scope.hiremeserver.repository.PollRepository
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.repository.VoteRepository
import kz.scope.hiremeserver.security.CurrentUser
import kz.scope.hiremeserver.security.UserPrincipal
import kz.scope.hiremeserver.service.PollService
import kz.scope.hiremeserver.util.AppConstants
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

/**
 * Created by rajeevkumarsingh on 20/11/17.
 */
private val logger = LoggerFactory.getLogger(PollController::class.java)

@RestController
@RequestMapping("/api/polls")
open class PollController {

    @Autowired
    private lateinit var pollRepository: PollRepository

    @Autowired
    private lateinit var voteRepository: VoteRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var pollService: PollService

    @GetMapping
    fun getPolls(@CurrentUser currentUser: UserPrincipal,
                 @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) page: Int,
                 @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) size: Int): PagedResponse<PollResponse> {
        return pollService.getAllPolls(currentUser, page, size)
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    fun createPoll(@Valid @RequestBody pollRequest: PollRequest): ResponseEntity<*> {
        val poll = pollService.createPoll(pollRequest)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest().path("/{pollId}")
            .buildAndExpand(poll.id).toUri()

        return ResponseEntity.created(location)
            .body(ApiResponse(true, "Poll Created Successfully"))
    }


    @GetMapping("/{pollId}")
    fun getPollById(@CurrentUser currentUser: UserPrincipal,
                    @PathVariable pollId: Long): PollResponse {
        return pollService.getPollById(pollId, currentUser)
    }

    @PostMapping("/{pollId}/votes")
    @PreAuthorize("hasRole('USER')")
    fun castVote(@CurrentUser currentUser: UserPrincipal,
                 @PathVariable pollId: Long?,
                 @Valid @RequestBody voteRequest: VoteRequest): PollResponse {
        return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, currentUser)
    }

}
