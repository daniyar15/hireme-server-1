package kz.scope.hiremeserver.payload

import java.time.Instant

data class JobOfferResponce (
        var id: Long,
        var description_of_responsibilities: String,
        var skills: String,
        var role: String,
        var company_id: Long,
        var job_type: String,
        var created_at: Instant,
        var updated_at: Instant
)
