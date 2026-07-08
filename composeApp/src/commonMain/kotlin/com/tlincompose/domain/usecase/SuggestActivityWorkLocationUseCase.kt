package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityWorkLocation

class SuggestActivityWorkLocationUseCase {
    operator fun invoke(existingLocations: List<ActivityWorkLocation>): ActivityWorkLocation {
        val relevantLocations = existingLocations.filter { it != ActivityWorkLocation.CLIENT_SITE }
        val officeCount = relevantLocations.count { it == ActivityWorkLocation.OFFICE }
        val totalCountAfterNextActivity = relevantLocations.size + 1

        val officeError = kotlin.math.abs(((officeCount + 1).toDouble() / totalCountAfterNextActivity) - OFFICE_TARGET_RATIO)
        val smartWorkingError = kotlin.math.abs((officeCount.toDouble() / totalCountAfterNextActivity) - OFFICE_TARGET_RATIO)

        return if (officeError < smartWorkingError) {
            ActivityWorkLocation.OFFICE
        } else {
            ActivityWorkLocation.SMART_WORKING
        }
    }

    private companion object {
        const val OFFICE_TARGET_RATIO = 0.3
    }
}
