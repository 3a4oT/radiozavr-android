package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import com.rovenskyi.radio_lux_fm_lviv_streamer.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectConfig @Inject constructor() {
    val streamUrl: String = BuildConfig.STREAM_URL
}
