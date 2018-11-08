package kz.scope.hiremeserver.payload

data class PostRequest(
        var company: Boolean,
        var author: Long,
        var title: String,
        var text: String,
        var jobOffersIds: List<Long>
)