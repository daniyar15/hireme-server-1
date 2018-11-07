package kz.scope.hiremeserver.payload

import java.util.*

data class CompanyRequest(
        var name: String,
        var creator: CompanyCreator,
        var logo_url: String,
        var location: String,
        var employee_number: Int,
        var specialization: String,
        var description: String,
        var createdAt: Date)