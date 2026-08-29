package com.rovenskyi.radiozavr.ui.widget.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radiozavr.R
import com.rovenskyi.radiozavr.core.models.weather.WeatherCondition
import com.rovenskyi.radiozavr.core.theme.LocalDimensions
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val WEATHER_ICON_SIZE = 28.dp
private val HUMIDITY_ICON_SIZE = 18.dp
private const val ROTATION_INTERVAL_MS = 20_000L

private data class WeatherSlot(
    val timeLabel: String,
    val temperatureCelsius: Double,
    val condition: WeatherCondition,
    val humidityPercent: Int?,
)

/**
 * Small persistent widget showing the weather in Lviv. Rotates in place between the current
 * reading and the next few hourly forecasts, mirroring the riddle widget's rotation pattern
 * instead of cramming everything into one busy row.
 * Renders nothing while loading or on error, to avoid cluttering the screen.
 */
@Composable
fun WeatherWidget(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    viewModel: WeatherWidgetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snapshot = uiState.snapshot

    if (uiState.isError || snapshot == null) return

    val nowLabel = stringResource(R.string.weather_now)
    val forecastSlots = snapshot.hourlyForecast.map { forecast ->
        WeatherSlot(
            timeLabel = stringResource(R.string.weather_hour_offset, forecast.hourOffset),
            temperatureCelsius = forecast.temperatureCelsius,
            condition = forecast.condition,
            humidityPercent = null,
        )
    }
    val slots = remember(snapshot, nowLabel, forecastSlots) {
        listOf(WeatherSlot(nowLabel, snapshot.temperatureCelsius, snapshot.condition, snapshot.humidityPercent)) +
            forecastSlots
    }
    var slotIndex by remember(slots) { mutableIntStateOf(0) }

    LaunchedEffect(slots) {
        while (true) {
            delay(ROTATION_INTERVAL_MS)
            slotIndex = (slotIndex + 1) % slots.size
        }
    }

    AnimatedContent(
        targetState = slots[slotIndex],
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "weather_rotation",
        modifier = modifier,
    ) { slot ->
        WeatherSlotContent(slot, compact = compact)
    }
}

@Composable
private fun WeatherSlotContent(slot: WeatherSlot, compact: Boolean) {
    val dimensions = LocalDimensions.current
    val conditionLabel = stringResource(slot.condition.labelRes())
    val temperature = slot.temperatureCelsius.roundToInt()
    val description = if (slot.humidityPercent != null) {
        stringResource(R.string.weather_content_description, conditionLabel, temperature, slot.humidityPercent)
    } else {
        stringResource(R.string.weather_forecast_content_description, slot.timeLabel, conditionLabel, temperature)
    }

    if (compact) {
        // Single row: on TV the widget shares the status line with the clock, so vertical space
        // it takes is space the riddle loses.
        Row(
            modifier = Modifier.semantics { contentDescription = description },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = slot.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(dimensions.spacingSmall))
            WeatherReading(slot, temperature, dimensions.spacingSmall)
        }
        return
    }

    Column(
        modifier = Modifier
            .padding(vertical = dimensions.spacingSmall)
            .semantics { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = slot.timeLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        WeatherReading(slot, temperature, dimensions.spacingSmall)
    }
}

@Composable
private fun WeatherReading(slot: WeatherSlot, temperature: Int, spacing: Dp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = slot.condition.icon(),
            contentDescription = null,
            modifier = Modifier.width(WEATHER_ICON_SIZE),
        )
        Spacer(modifier = Modifier.width(spacing))
        Text(text = "$temperature°C", style = MaterialTheme.typography.titleLarge)
        if (slot.humidityPercent != null) {
            Spacer(modifier = Modifier.width(spacing))
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = null,
                modifier = Modifier.width(HUMIDITY_ICON_SIZE),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${slot.humidityPercent}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun WeatherCondition.labelRes(): Int = when (this) {
    WeatherCondition.CLEAR -> R.string.weather_condition_clear
    WeatherCondition.PARTLY_CLOUDY -> R.string.weather_condition_partly_cloudy
    WeatherCondition.CLOUDY -> R.string.weather_condition_cloudy
    WeatherCondition.FOG -> R.string.weather_condition_fog
    WeatherCondition.DRIZZLE -> R.string.weather_condition_drizzle
    WeatherCondition.RAIN -> R.string.weather_condition_rain
    WeatherCondition.SNOW -> R.string.weather_condition_snow
    WeatherCondition.THUNDERSTORM -> R.string.weather_condition_thunderstorm
    WeatherCondition.UNKNOWN -> R.string.weather_condition_unknown
}

private fun WeatherCondition.icon(): ImageVector = when (this) {
    WeatherCondition.CLEAR -> Icons.Filled.WbSunny
    WeatherCondition.PARTLY_CLOUDY -> Icons.Filled.WbCloudy
    WeatherCondition.CLOUDY -> Icons.Filled.Cloud
    WeatherCondition.FOG -> Icons.Filled.CloudQueue
    WeatherCondition.DRIZZLE -> Icons.Filled.Grain
    WeatherCondition.RAIN -> Icons.Filled.Umbrella
    WeatherCondition.SNOW -> Icons.Filled.AcUnit
    WeatherCondition.THUNDERSTORM -> Icons.Filled.Thunderstorm
    WeatherCondition.UNKNOWN -> Icons.Filled.DeviceThermostat
}
