package kz.scope.hiremeserver.payload

import javax.validation.constraints.NotNull

class VoteRequest(
    @NotNull
    var choiceId: Long
)
