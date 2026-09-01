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

    override fun observeAllCustomizations(): Flow<List<AppCustomizationEntity>> {
        return dao.observeAllCustomizations()
    }

    override fun observeCustomLabels(): Flow<Map<String, String>> {
        return dao.observeCustomLabels().map { tuples ->
            tuples.filter { it.customLabel != null }
                .associate { it.packageName to it.customLabel!! }
        }
    }

    override fun observeHiddenPackages(): Flow<Set<String>> {
        return dao.observeHiddenPackages().map { it.toSet() }
    }

    override suspend fun setCustomLabel(packageName: String, label: String?) {
        val existing = dao.getCustomizationForPackage(packageName)
        if (existing == null) {
            dao.upsertCustomization(
                AppCustomizationEntity(
                    packageName = packageName,
                    customLabel = label
                )
            )
        } else {
            dao.updateCustomLabel(packageName, label)
        }
    }

    override suspend fun setPackageHidden(packageName: String, isHidden: Boolean) {
        val existing = dao.getCustomizationForPackage(packageName)
        if (existing == null) {
            dao.upsertCustomization(
                AppCustomizationEntity(
                    packageName = packageName,
                    isHidden = isHidden
                )
            )
        } else {
            dao.updateHiddenStatus(packageName, isHidden)
        }
    }

    override suspend fun updateIconCustomization(
        packageName: String,
        iconUri: String?,
        colorHex: String?,
        glyphName: String?,
        shape: String?
    ) {
        val existing = dao.getCustomizationForPackage(packageName)
        if (existing == null) {
            dao.upsertCustomization(
                AppCustomizationEntity(
                    packageName = packageName,
                    customIconUri = iconUri,
                    customIconColorHex = colorHex,
                    customGlyphName = glyphName,
                    customIconShape = shape
                )
            )
        } else {
            dao.updateIconCustomization(packageName, iconUri, colorHex, glyphName, shape)
        }
    }

    override suspend fun resetIconCustomization(packageName: String) {
        updateIconCustomization(packageName, null, null, null, null)
    }
}
