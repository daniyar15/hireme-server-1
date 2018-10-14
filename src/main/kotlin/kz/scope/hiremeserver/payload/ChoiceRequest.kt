package kz.scope.hiremeserver.payload

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ChoiceRequest(
    @NotBlank
    @Size(max = 40)
    var text: String
)
