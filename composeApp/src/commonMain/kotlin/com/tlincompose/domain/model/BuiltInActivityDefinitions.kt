package com.tlincompose.domain.model

enum class DefaultExtWorkMode {
    SMART_WORKING,
    OFFICE,
}

object BuiltInActivityDefinitions {
    const val DefaultExtCode: String = "EXT-DEFAULT"
    const val BloodDonationCode: String = "EXT-SANGUE"
    const val MedicalVisitCode: String = "EXT-VISITA"

    val ProtectedCodes: Set<String> = setOf(
        DefaultExtCode,
        BloodDonationCode,
        MedicalVisitCode,
    )

    fun isProtectedCode(extCode: String): Boolean = extCode in ProtectedCodes
}
