package com.rovenskyi.radiozavr.data.widget.riddle

import com.rovenskyi.radiozavr.core.models.riddle.RiddleInterval
import com.rovenskyi.radiozavr.core.models.riddle.RiddleRotationState
import com.rovenskyi.radiozavr.domain.widget.riddle.RiddleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton service that manages riddle rotation.
 *
 * Responsibilities:
 * - Loading riddles from repository
 * - Timer-based rotation at configured interval
 * - Tracking rotation state (current riddle, start time, index)
 *
 * Hot state holder following architecture guidelines:
 * - Uses MutableStateFlow for imperative updates
 * - Exposes immutable StateFlow for observers
 */
@Singleton
class RiddleRotationService @Inject constructor(
    private val riddleRepository: RiddleRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _rotationState = MutableStateFlow(RiddleRotationState())
    val rotationState: StateFlow<RiddleRotationState> = _rotationState.asStateFlow()

    private var rotationJob: Job? = null
    private var currentInterval: RiddleInterval? = null
    private var isPaused = false
    private var pauseStartMs = 0L

    init {
        loadNextRiddle()
    }

    /**
     * Starts or restarts riddle rotation with the given interval.
     * If interval changed, loads a new riddle immediately to sync animation.
     * If currently paused, only updates the interval — rotation stays paused.
     */
    fun setInterval(interval: RiddleInterval) {
        val intervalChanged = currentInterval != interval
        currentInterval = interval

        rotationJob?.cancel()
        rotationJob = null

        // Load new riddle immediately when interval changes (syncs animation with timer)
        if (intervalChanged) {
            pauseStartMs = 0L
            loadNextRiddle()
        }

        if (!isPaused) {
            val initialDelay = if (intervalChanged) {
                interval.milliseconds // new riddle just loaded — wait full interval
            } else {
                val elapsed = System.currentTimeMillis() - _rotationState.value.startTimeMillis
                (interval.milliseconds - elapsed).coerceAtLeast(0L)
            }
            startRotationJob(interval, initialDelay)
        }
    }

    /**
     * Pauses riddle rotation and freezes the visual timer.
     * Call when the widget leaves the screen.
     */
    fun pause() {
        if (isPaused) return
        isPaused = true
        pauseStartMs = System.currentTimeMillis()
        rotationJob?.cancel()
        rotationJob = null
    }

    /**
     * Resumes riddle rotation from where it was paused.
     * Adjusts [RiddleRotationState.startTimeMillis] to compensate for the paused
     * duration so the visual timer continues from the correct position.
     */
    fun resume() {
        if (!isPaused) return
        isPaused = false

        // Capture once so both pausedFor and elapsed use the same reference point,
        // avoiding timing drift between the two calculations.
        val now = System.currentTimeMillis()
        val pausedFor = now - pauseStartMs
        pauseStartMs = 0L

        // Shift startTimeMillis forward so elapsed time appears frozen during pause
        if (pausedFor > 0) {
            _rotationState.update { it.copy(startTimeMillis = it.startTimeMillis + pausedFor) }
        }

        val interval = currentInterval ?: return
        val elapsed = now - _rotationState.value.startTimeMillis
        val remaining = (interval.milliseconds - elapsed).coerceAtLeast(0L)
        startRotationJob(interval, remaining)
    }

    private fun startRotationJob(interval: RiddleInterval, initialDelay: Long) {
        rotationJob = scope.launch {
            delay(initialDelay)
            loadNextRiddle()
            while (isActive) {
                delay(interval.milliseconds)
                loadNextRiddle()
            }
        }
    }

    private fun loadNextRiddle() {
        scope.launch {
            val riddle = riddleRepository.getNextUniqueRiddle()
            _rotationState.update { state ->
                RiddleRotationState(
                    riddle = riddle,
                    startTimeMillis = System.currentTimeMillis(),
                    index = state.index + 1,
                )
            }
        }
    }
}
