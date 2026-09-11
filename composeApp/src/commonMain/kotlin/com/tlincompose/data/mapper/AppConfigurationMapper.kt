package com.tlincompose.data.mapper

import com.tlincompose.data.local.AppConfigurationStore
import com.tlincompose.domain.model.AppConfigurationData

fun AppConfigurationStore.toDomain(): AppConfigurationData = AppConfigurationData(
    preferences = preferences.toDomain(),
    activityDefinitions = activityDefinitions.map { it.toDomain() },
)

fun AppConfigurationData.toStore(): AppConfigurationStore = AppConfigurationStore(
    preferences = preferences.toStore(),
    activityDefinitions = activityDefinitions.map { it.toEntity() },
)
