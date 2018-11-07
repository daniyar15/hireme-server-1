package kz.scope.hiremeserver.payload


data class StudentProfile(
    var username: String,
    var fullname: String,
    var location : String,
    var employment: Employment,
    var current_role : String,
    var education : Education,
    var hidden : Boolean,
    var job_type : String,
    var job_field : String,
    var skills: String
)
