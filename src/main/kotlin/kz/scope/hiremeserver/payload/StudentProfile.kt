package kz.scope.hiremeserver.payload

import java.time.Instant

data class StudentProfile(
    var id: Long,
    var username: String,
    var fullname: String,
    var email: String,
    //var joinedAt: Instant,
    var sOrGrad : Boolean,
    var location : String,
    var fields : String,
    var education : String,
    var degree : String,
    var display : Boolean,
    var jobType : String,
    var roleSpecification : String,
    var skillSet: String
)
