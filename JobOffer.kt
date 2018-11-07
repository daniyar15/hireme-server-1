package kz.scope.hiremeserver.payload

import java.time.Instant

data class JobOffer (
    var id: Long,
    var role: String,
    var description_of_responsibilities: String,
    var job_type: String,
    var skills: String,
    var created_at: Instant
)
