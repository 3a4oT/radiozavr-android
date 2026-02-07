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

    init {
        loadNextRiddle()
    }

    /**
     * Starts or restarts riddle rotation with the given interval.
     * If interval changed, loads a new riddle immediately to sync animation.
     */
    fun setInterval(interval: RiddleInterval) {
        val intervalChanged = currentInterval != interval
        currentInterval = interval

        rotationJob?.cancel()

        // Load new riddle immediately when interval changes (syncs animation with timer)
        if (intervalChanged) {
            loadNextRiddle()
        }

        rotationJob = scope.launch {
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
