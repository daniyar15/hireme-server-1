package kz.scope.hiremeserver.payload

import java.time.Instant

data class PostResponse (
        var id: Long,
        var сompany: Boolean,
        var author: Long,
        var title: String,
        var text: String,
        var jobOffers: List<JobOfferResponse>,
        var createdAt: Instant
)