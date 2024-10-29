package com.rovenskyi.radio_lux_fm_lviv_streamer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.RadioPlayerScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.theme.RadioLuxFmLvivStreamerTheme
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RadioPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val radioPlayerViewModel: RadioPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RadioLuxFmLvivStreamerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RadioPlayerScreen(viewModel = radioPlayerViewModel)
                }
            }
        }
    }
}
