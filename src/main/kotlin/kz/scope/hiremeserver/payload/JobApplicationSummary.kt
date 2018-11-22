package kz.scope.hiremeserver.payload

import java.time.Instant

data class JobApplicationSummary(
        var id: Long,
        var userSummary: UserSummary,
        var job_offer_summary: JobOfferSummary,
        var created_at: Instant
)