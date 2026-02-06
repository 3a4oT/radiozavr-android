package com.rovenskyi.radiozavr.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.rovenskyi.radiozavr.core.models.platform.DeviceType
import com.rovenskyi.radiozavr.domain.repository.PlatformRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Platform detection based on system features and display characteristics.
 *
 * Uses Google Play heuristics for device classification:
 * https://android-developers.googleblog.com/2023/06/detecting-if-device-is-foldable-tablet.html
 */
@Singleton
class PlatformRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : PlatformRepository {

    override val deviceType: DeviceType by lazy {
        val pm = context.packageManager
        when {
            pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK) -> DeviceType.TV
            pm.hasSystemFeature(PackageManager.FEATURE_WATCH) -> DeviceType.WATCH
            pm.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE) -> DeviceType.AUTOMOTIVE
            hasHingeSensor() && isLargeBuiltInDisplay() -> DeviceType.FOLDABLE
            isLargeBuiltInDisplay() -> DeviceType.TABLET
            else -> DeviceType.PHONE
        }
    }

    override val isTv: Boolean by lazy {
        deviceType == DeviceType.TV
    }

    override val isLargeScreen: Boolean by lazy {
        deviceType in setOf(DeviceType.TV, DeviceType.TABLET, DeviceType.FOLDABLE)
    }

    private fun hasHingeSensor(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_HINGE_ANGLE)
        } else {
            false
        }
    }

    private fun isLargeBuiltInDisplay(): Boolean {
        val config = context.resources.configuration
        return config.smallestScreenWidthDp >= LARGE_SCREEN_SMALLEST_WIDTH_DP
    }

    companion object {
        private const val LARGE_SCREEN_SMALLEST_WIDTH_DP = 600
    }
}
