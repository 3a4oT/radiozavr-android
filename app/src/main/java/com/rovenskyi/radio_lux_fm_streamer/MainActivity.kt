package com.rovenskyi.radio_lux_fm_streamer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rovenskyi.radio_lux_fm_streamer.ui.RadioScreen
import com.rovenskyi.radio_lux_fm_streamer.ui.theme.LuxFMRadioStreamingTheme
import com.rovenskyi.radio_lux_fm_streamer.viewmodel.RadioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val radioViewModel: RadioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LuxFMRadioStreamingTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RadioScreen(viewModel = radioViewModel)
                }
            }
        }
    }
}
