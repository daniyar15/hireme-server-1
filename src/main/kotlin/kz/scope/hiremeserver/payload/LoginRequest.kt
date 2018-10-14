package kz.scope.hiremeserver.payload

import javax.validation.constraints.NotBlank

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */
data class LoginRequest(
    @NotBlank
    var usernameOrEmail: String,

    @NotBlank
    var password: String
)
