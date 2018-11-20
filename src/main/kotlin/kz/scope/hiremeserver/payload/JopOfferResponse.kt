package kz.scope.hiremeserver.payload

import java.time.Instant

data class JobOfferResponse (
        var id: Long,
        var company: CompanyJobOfferResponse,
        var position: String,
        var responsibilities: String,
        var qualifications: String,
        var locations: List<String>,
        var created_at: Instant,
        var updated_at: Instant
)
