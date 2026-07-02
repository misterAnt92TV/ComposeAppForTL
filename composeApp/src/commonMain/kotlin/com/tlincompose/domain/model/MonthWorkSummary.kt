package com.tlincompose.domain.model

data class MonthWorkSummary(
    val totalLoggedMinutes: Int,
    val targetCompletionMinutes: Int,
    val completedCompletionMinutes: Int,
    val remainingCompletionMinutes: Int,
) {
    val completionFraction: Float
        get() = if (targetCompletionMinutes == 0) 1f else completedCompletionMinutes.toFloat() / targetCompletionMinutes.toFloat()
}
