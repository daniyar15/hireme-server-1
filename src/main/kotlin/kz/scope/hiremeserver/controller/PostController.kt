package kz.scope.hiremeserver.controller

import kz.scope.hiremeserver.exception.ResourceNotFoundException
import kz.scope.hiremeserver.model.Company
import kz.scope.hiremeserver.model.Post
import kz.scope.hiremeserver.model.User
import kz.scope.hiremeserver.payload.*
import kz.scope.hiremeserver.repository.CompanyRepository
import kz.scope.hiremeserver.repository.JobOfferRepository
import kz.scope.hiremeserver.repository.PostRepository
import kz.scope.hiremeserver.repository.UserRepository
import kz.scope.hiremeserver.security.CurrentUser
import kz.scope.hiremeserver.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

private val logger1 = LoggerFactory.getLogger(PostController::class.java)

@RestController
@RequestMapping("/api")
class PostController {

    @Autowired
    lateinit var postRepository: PostRepository

    @Autowired
    lateinit var companyRepository: CompanyRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jobOfferRepository: JobOfferRepository

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

    @PostMapping("post")
    @PreAuthorize("hasRole('USER')")
    fun addPost(@CurrentUser currentUser: UserPrincipal, @Valid @RequestBody postRequest: PostRequest)
            : ResponseEntity<*> {
        if (postRequest.company) {
            // author of the post is company
            val companyOptional = companyRepository.findById(postRequest.author)

            if (companyOptional.isPresent.not()) {
                return ResponseEntity(ApiResponse(false, "Such company does not exists"), HttpStatus.EXPECTATION_FAILED)
            }

            // getting current user of class User
            val current_user_id = currentUser.id
            val current_user_optional = userRepository.findById(current_user_id)
            val current_user: User

            if (current_user_optional.isPresent) {
                current_user = current_user_optional.get()
            } else {
                throw ResourceNotFoundException("User", "id", current_user_id)
            }

            // getting all companies associated with the current users
            val employers = current_user.managing

            val companies: MutableList<Company> = ArrayList<Company>()

            for (employer in employers) {
                companies.add(employer.company)
            }

            var count = 0
            for (company_candidate in companies) {
                if (company_candidate.id == postRequest.author) {
                    count+=1
                }
            }

            if (count == 0) {
                return ResponseEntity(ApiResponse(false, "You can only post for a company managed by your account."), HttpStatus.UNAUTHORIZED)
            }

            val post = Post(postRequest.company, postRequest.author, postRequest.title, postRequest.text)

            for (jobOfferId in postRequest.jobOffersIds) {
                val jobOfferOptional = jobOfferRepository.findById(jobOfferId)
                if(jobOfferOptional.isPresent) post.jobOffers.add(jobOfferOptional.get())
                else return ResponseEntity(ApiResponse(false, "Job Offer with one of ids does not exists")
                        , HttpStatus.EXPECTATION_FAILED)
            }

            val result = postRepository.save(post)
            val location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/posts/{id}")
                    .buildAndExpand(result.id).toUri()

            return ResponseEntity.created(location).body(ApiResponse(true, "Post created successfully"))

        } else {
            // author of the post is user
            val userOptional = userRepository.findById(postRequest.author)

            if (userOptional.isPresent.not()) {
                return ResponseEntity(ApiResponse(false, "Such user does not exists"), HttpStatus.EXPECTATION_FAILED)
            } else if (userOptional.get().id != currentUser.id) {
                return ResponseEntity(ApiResponse(false, "You can only create post on your account."), HttpStatus.UNAUTHORIZED)
            }

            val post = Post(postRequest.company, postRequest.author, postRequest.title, postRequest.text)

            for (jobOfferId in postRequest.jobOffersIds) {
                val jobOfferOptional = jobOfferRepository.findById(jobOfferId)
                if(jobOfferOptional.isPresent) post.jobOffers.add(jobOfferOptional.get())
                else return ResponseEntity(ApiResponse(false, "Job Offer with one of ids does not exists")
                        , HttpStatus.EXPECTATION_FAILED)
            }

            val result = postRepository.save(post)
            val location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/posts/{id}")
                    .buildAndExpand(result.id).toUri()

            return ResponseEntity.created(location).body(ApiResponse(true, "Post created successfully"))
        }
    }

}