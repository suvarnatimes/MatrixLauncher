package com.matrixlauncher.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppDatabaseRepository {
    /**
     * Map of packageName -> custom display label.
     */
    fun observeCustomLabels(): Flow<Map<String, String>>

    /**
     * Set of packageNames marked as hidden.
     */
    fun observeHiddenPackages(): Flow<Set<String>>

    suspend fun setCustomLabel(packageName: String, label: String?)

    suspend fun setPackageHidden(packageName: String, isHidden: Boolean)
}
