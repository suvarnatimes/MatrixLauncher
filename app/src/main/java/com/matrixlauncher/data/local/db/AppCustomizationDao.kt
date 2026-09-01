package com.matrixlauncher.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppCustomizationDao {

    @Query("SELECT * FROM app_customizations")
    fun observeAllCustomizations(): Flow<List<AppCustomizationEntity>>

    @Query("SELECT * FROM app_customizations WHERE packageName = :packageName LIMIT 1")
    suspend fun getCustomizationForPackage(packageName: String): AppCustomizationEntity?

    @Query("SELECT packageName, customLabel FROM app_customizations WHERE customLabel IS NOT NULL")
    fun observeCustomLabels(): Flow<List<CustomLabelTuple>>

    @Query("SELECT packageName FROM app_customizations WHERE isHidden = 1")
    fun observeHiddenPackages(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomization(entity: AppCustomizationEntity)

    @Query("UPDATE app_customizations SET customLabel = :label WHERE packageName = :packageName")
    suspend fun updateCustomLabel(packageName: String, label: String?)

    @Query("UPDATE app_customizations SET isHidden = :isHidden WHERE packageName = :packageName")
    suspend fun updateHiddenStatus(packageName: String, isHidden: Boolean)

    @Query("UPDATE app_customizations SET customIconUri = :iconUri, customIconColorHex = :colorHex, customGlyphName = :glyphName, customIconShape = :shape WHERE packageName = :packageName")
    suspend fun updateIconCustomization(
        packageName: String,
        iconUri: String?,
        colorHex: String?,
        glyphName: String?,
        shape: String?
    )

    @Query("DELETE FROM app_customizations WHERE packageName = :packageName")
    suspend fun deleteCustomization(packageName: String)
}

data class CustomLabelTuple(
    val packageName: String,
    val customLabel: String?
)
