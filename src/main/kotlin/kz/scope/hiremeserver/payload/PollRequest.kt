package kz.scope.hiremeserver.payload

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

public data class PollRequest(
    @NotBlank
    @Size(max = 140)
    public var question: String,

    @NotNull
    @Size(min = 2, max = 6)
    @Valid
    public var choices: List<ChoiceRequest>,

    @NotNull
    @Valid
    public var pollLength: PollLength
)
