package com.rovenskyi.radio_lux_fm_lviv_streamer.service
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerEventReceiver @Inject constructor() {

    private val _playerErrorLiveData = MutableLiveData<String?>()
    val playerErrorLiveData: LiveData<String?> get() = _playerErrorLiveData

    private val _playerIsLoadingLiveData = MutableLiveData<Boolean>()
    val playerIsLoadingLiveData: LiveData<Boolean> get() = _playerIsLoadingLiveData

    fun postPlayerError(message: String?) {
        _playerErrorLiveData.postValue(message)
    }

    fun clearPlayerErrorMessage() {
        _playerErrorLiveData.postValue(null)
    }

    fun postPlayerIsLoading(isLoading: Boolean) {
        _playerIsLoadingLiveData.postValue(isLoading)
    }
}

