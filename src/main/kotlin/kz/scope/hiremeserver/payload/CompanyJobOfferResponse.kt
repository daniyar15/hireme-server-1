package kz.scope.hiremeserver.payload

data class CompanyJobOfferResponse(
        var company_id: Long,
        var name: String,
        var logo: String
)