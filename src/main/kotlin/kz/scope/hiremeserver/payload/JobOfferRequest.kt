package kz.scope.hiremeserver.payload

data class JobOfferRequest (
        var company: CompanyJobOfferRequest,
        var position: String,
        var responsibilities: String,
        var qualifications: String,
        var locations: List<String>
)