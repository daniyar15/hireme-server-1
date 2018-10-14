package kz.scope.hiremeserver.payload

import javax.validation.constraints.Max
import javax.validation.constraints.NotNull

data class PollLength(
    @NotNull
    @Max(7) var days: Int,

    @NotNull
    @Max(23)
    var hours: Int
)
