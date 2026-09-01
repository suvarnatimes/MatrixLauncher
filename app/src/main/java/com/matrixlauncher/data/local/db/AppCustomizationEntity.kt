package com.matrixlauncher.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_customizations")
data class AppCustomizationEntity(
    @PrimaryKey
    val packageName: String,
    val customLabel: String? = null,
    val isHidden: Boolean = false,
    val customIconUri: String? = null,
    val customIconColorHex: String? = null,
    val customGlyphName: String? = null,
    val customIconShape: String? = null
)
