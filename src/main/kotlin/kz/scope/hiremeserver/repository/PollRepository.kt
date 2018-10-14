package kz.scope.hiremeserver.repository

import kz.scope.hiremeserver.model.Poll
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Created by rajeevkumarsingh on 20/11/17.
 */
@Repository
interface PollRepository : JpaRepository<Poll, Long> {

    override fun findById(pollId: Long): Optional<Poll>

    fun findByCreatedBy(userId: Long?, pageable: Pageable): Page<Poll>

    fun countByCreatedBy(userId: Long?): Long

    fun findByIdIn(pollIds: List<Long>): List<Poll>

    fun findByIdIn(pollIds: List<Long>, sort: Sort): List<Poll>
}
