package com.tlincompose.domain.model

object BuiltInActivityDefinitions {
    const val BloodDonationCode: String = "EXT-SANGUE"
    const val MedicalVisitCode: String = "EXT-VISITA"

    val ProtectedCodes: Set<String> = setOf(
        BloodDonationCode,
        MedicalVisitCode,
    )

    fun isProtectedCode(extCode: String): Boolean = extCode in ProtectedCodes
}
