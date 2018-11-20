package kz.scope.hiremeserver.controller

class MockUser (
        val username: String,
        val password: String,
        val email: String,
        val fullname: String
)

val existingUser = MockUser("the_7th_hokage", "kurama", "the_7th@nu.edu.kz", "Naruto Uzumaki")

val nonExistingUser = MockUser("steve_wolfe", "secret", "steve.wolfe@nu.edu.kz", "Johnny Sins")