package kz.scope.hiremeserver.payload

import javax.validation.constraints.NotBlank

public data class LoginRequest(
    @NotBlank
    var usernameOrEmail: String,
    @NotBlank
    var password: String
)
