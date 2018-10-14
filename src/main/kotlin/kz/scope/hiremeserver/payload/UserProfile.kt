package kz.scope.hiremeserver.payload

import java.time.Instant

data class UserProfile(
    var id: Long,
    var username: String,
    var fullname: String,
    var joinedAt: Instant
//TODO: Additional info about user (location, education etc)
)
