package com.matrixlauncher.domain.repository

import com.matrixlauncher.data.local.db.AppCustomizationEntity
import kotlinx.coroutines.flow.Flow

interface AppDatabaseRepository {
    fun observeAllCustomizations(): Flow<List<AppCustomizationEntity>>
    fun observeCustomLabels(): Flow<Map<String, String>>
    fun observeHiddenPackages(): Flow<Set<String>>
    suspend fun setCustomLabel(packageName: String, label: String?)
    suspend fun setPackageHidden(packageName: String, isHidden: Boolean)
    suspend fun updateIconCustomization(
        packageName: String,
        iconUri: String?,
        colorHex: String?,
        glyphName: String?,
        shape: String?
    )
    suspend fun resetIconCustomization(packageName: String)
}
