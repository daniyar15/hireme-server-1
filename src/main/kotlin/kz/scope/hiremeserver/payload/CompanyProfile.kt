package kz.scope.hiremeserver.payload

import jdk.nashorn.internal.codegen.ApplySpecialization

data class CompanyProfile(
        var name: String,
        var location: String,
        var logo: String,
        var numEmployees: Int,
        var specialization: String,
        var description: String,
        var managerName: String
)
