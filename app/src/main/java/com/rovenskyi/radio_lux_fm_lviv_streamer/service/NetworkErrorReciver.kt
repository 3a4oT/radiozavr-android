package com.rovenskyi.radio_lux_fm_lviv_streamer.service
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkErrorReceiver @Inject constructor() {

    private val _networkErrorLiveData = MutableLiveData<String?>()
    val networkErrorLiveData: LiveData<String?> get() = _networkErrorLiveData

    fun postNetworkError(message: String?) {
        _networkErrorLiveData.postValue(message)
    }

    fun clearErrorMessage() {
        _networkErrorLiveData.postValue(null)
    }
}

