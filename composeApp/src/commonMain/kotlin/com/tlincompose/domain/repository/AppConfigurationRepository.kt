package com.tlincompose.domain.repository

import com.tlincompose.domain.model.AppConfigurationData
import com.tlincompose.domain.model.AppConfigurationReadResult
import com.tlincompose.domain.model.ExportDocument

interface AppConfigurationRepository {
    suspend fun exportConfiguration(data: AppConfigurationData, fileName: String): ExportDocument
    suspend fun readConfiguration(bytes: ByteArray): AppConfigurationReadResult
}
