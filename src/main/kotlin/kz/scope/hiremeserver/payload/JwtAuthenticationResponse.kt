package kz.scope.hiremeserver.payload

public data class JwtAuthenticationResponse(var accessToken: String) {
    var tokenType = "Bearer"
}