package kz.scope.hiremeserver.payload

data class JobOfferRequest (
        var description_of_responsibilities: String,
        var skills: String,
        var role: String,
        var company_id: Long,
        var job_type: String
)