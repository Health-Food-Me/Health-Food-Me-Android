package org.helfoome.util

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceInfo @Inject constructor(@ApplicationContext val context: Context) {

    fun getDeviceModel(): String {
        return Build.MODEL
    }

    fun getDeviceOs(): String {
        return Build.VERSION.RELEASE.toString()
    }

    fun getAppVersion(): String {
        val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return info.versionName
    }
}
