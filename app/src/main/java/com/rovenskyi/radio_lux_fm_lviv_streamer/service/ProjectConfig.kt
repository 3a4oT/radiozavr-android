package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectConfig @Inject constructor() {
    val streamUrl: String = "http://streamvideo.luxnet.ua/luxlviv/luxlviv.stream/chunklist.m3u8"
}
