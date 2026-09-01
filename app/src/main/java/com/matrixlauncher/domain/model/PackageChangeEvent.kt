package com.matrixlauncher.domain.model

import android.os.UserHandle

sealed interface PackageChangeEvent {
    data class PackageAdded(val packageName: String, val userHandle: UserHandle) : PackageChangeEvent
    data class PackageRemoved(val packageName: String, val userHandle: UserHandle) : PackageChangeEvent
    data class PackageChanged(val packageName: String, val userHandle: UserHandle) : PackageChangeEvent
    data class PackagesAvailable(val packageNames: Array<String>, val userHandle: UserHandle) : PackageChangeEvent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PackagesAvailable
            return packageNames.contentEquals(other.packageNames) && userHandle == other.userHandle
        }
        override fun hashCode(): Int {
            var result = packageNames.contentHashCode()
            result = 31 * result + userHandle.hashCode()
            return result
        }
    }
    data class PackagesUnavailable(val packageNames: Array<String>, val userHandle: UserHandle) : PackageChangeEvent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PackagesUnavailable
            return packageNames.contentEquals(other.packageNames) && userHandle == other.userHandle
        }
        override fun hashCode(): Int {
            var result = packageNames.contentHashCode()
            result = 31 * result + userHandle.hashCode()
            return result
        }
    }
}
