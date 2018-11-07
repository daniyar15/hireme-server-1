package kz.scope.hiremeserver.payload

import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CompanyRequest(
        @NotBlank
        @Size(max = 40)
        var name: String,

        var creator: CompanyCreator,
        var logo_url: String,

        @NotBlank
        @Size(max = 100)
        var location: String,
        var employee_number: Int,
        var specialization: String,
        var description: String
)