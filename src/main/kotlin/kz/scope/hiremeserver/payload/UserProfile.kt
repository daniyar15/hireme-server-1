package kz.scope.hiremeserver.payload

import java.time.Instant

data class UserProfile (

        var username: String,
        var fullname:String,
        var location : String,
        var education : Education,
        var hidden : Boolean,
        var strong_skill : StrongSkill,
        var urls: Urls,
        var skills: String,
        var employment: Employment,
        var avatar_url: String,
        var createdAt: Instant

)
