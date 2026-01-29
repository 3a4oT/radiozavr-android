package com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PlatformRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlatformRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : PlatformRepository {

    override val isTv: Boolean by lazy {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }
}
