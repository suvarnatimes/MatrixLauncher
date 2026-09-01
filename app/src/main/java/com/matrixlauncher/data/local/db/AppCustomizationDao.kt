package com.matrixlauncher.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppCustomizationDao {

    @Query("SELECT * FROM app_customizations")
    fun getAllCustomizations(): Flow<List<AppCustomizationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AppCustomizationEntity)

    @Query("SELECT * FROM app_customizations WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackageName(packageName: String): AppCustomizationEntity?

    @Query("UPDATE app_customizations SET customLabel = :customLabel, updatedAt = :updatedAt WHERE packageName = :packageName")
    suspend fun updateCustomLabel(packageName: String, customLabel: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE app_customizations SET isHidden = :isHidden, updatedAt = :updatedAt WHERE packageName = :packageName")
    suspend fun updateHiddenStatus(packageName: String, isHidden: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM app_customizations WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
}
