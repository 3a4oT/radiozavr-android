package com.rovenskyi.radiozavr.service

import com.rovenskyi.radiozavr.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectConfig @Inject constructor() {
    val streamUrl: String = BuildConfig.STREAM_URL
}
