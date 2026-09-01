package com.matrixlauncher.data.repository

import com.matrixlauncher.data.local.db.AppCustomizationDao
import com.matrixlauncher.data.local.db.AppCustomizationEntity
import com.matrixlauncher.domain.repository.AppDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDatabaseRepositoryImpl @Inject constructor(
    private val dao: AppCustomizationDao
) : AppDatabaseRepository {

    override fun observeCustomLabels(): Flow<Map<String, String>> {
        return dao.getAllCustomizations().map { list ->
            list.filter { !it.customLabel.isNullOrBlank() }
                .associate { it.packageName to it.customLabel!! }
        }
    }

    override fun observeHiddenPackages(): Flow<Set<String>> {
        return dao.getAllCustomizations().map { list ->
            list.filter { it.isHidden }.map { it.packageName }.toSet()
        }
    }

    override suspend fun setCustomLabel(packageName: String, label: String?) {
        val existing = dao.getByPackageName(packageName)
        if (existing != null) {
            dao.updateCustomLabel(packageName, label)
        } else {
            dao.upsert(AppCustomizationEntity(packageName = packageName, customLabel = label))
        }
    }

    override suspend fun setPackageHidden(packageName: String, isHidden: Boolean) {
        val existing = dao.getByPackageName(packageName)
        if (existing != null) {
            dao.updateHiddenStatus(packageName, isHidden)
        } else {
            dao.upsert(AppCustomizationEntity(packageName = packageName, isHidden = isHidden))
        }
    }
}
