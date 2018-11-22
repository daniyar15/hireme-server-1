package kz.scope.hiremeserver.payload

data class ApplicationByCompany(
        var company_name: String,
        var job_application_summaries: List<JobApplicationSummary>
)