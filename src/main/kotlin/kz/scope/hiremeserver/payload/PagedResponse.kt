package kz.scope.hiremeserver.payload

data class PagedResponse<T>(
    var content: List<T>,
    var page: Int = 0,
    var size: Int = 0,
    var totalElements: Long = 0,
    var totalPages: Int = 0,
    var isLast: Boolean = false
)
