package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Post
import kz.scope.hiremeserver.payload.JobOfferResponse
import kz.scope.hiremeserver.payload.PostResponse
import kz.scope.hiremeserver.repository.PostRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger1 = LoggerFactory.getLogger(PostController::class.java)

@RestController
@RequestMapping("/api")
class PostController {

    @Autowired
    lateinit var postRepository: PostRepository

    @GetMapping("/posts/{id}")
    @PreAuthorize("hasRole('USER')")
    fun getPost(@PathVariable(value = "id") id: Long): PostResponse {
        val postOptional = postRepository.findById(id)
        val post: Post

        if (postOptional.isPresent) {
            post = postOptional.get()
        } else {
            throw ResourceNotFoundException("Post", "id", id)
        }

        val jobOfferResponses: MutableList<JobOfferResponse> = ArrayList<JobOfferResponse>()
        for (jobOffer in post.jobOffers) {
            jobOfferResponses.add(JobOfferResponse(jobOffer.id, jobOffer.descriptionOfResponsibilities,jobOffer.skills,
                    jobOffer.role, jobOffer.company.id, jobOffer.jobType, jobOffer.createdAt, jobOffer.updatedAt))
        }

        return PostResponse(post.id, post.isCompany, post.authorId, post.title, post.text, jobOfferResponses, post.createdAt)
    }

}