package kz.scope.hiremeserver.payload

data class JobOfferRequest (
        var id: Long,
        var role: String,
        var description_of_responsibilities: String,
        var job_type: String,
        var skills: String
)