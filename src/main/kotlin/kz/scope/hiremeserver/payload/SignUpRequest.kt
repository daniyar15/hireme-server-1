package kz.scope.hiremeserver.payload

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

public data class SignUpRequest(
    @NotBlank
    @Size(min = 4, max = 40)
    var name: String,

    @NotBlank
    @Size(min = 3, max = 15)
    var username: String,

    @NotBlank
    @Size(max = 40)
    var email: String,

    @NotBlank
    @Size(min = 6, max = 20)
    var password: String
)