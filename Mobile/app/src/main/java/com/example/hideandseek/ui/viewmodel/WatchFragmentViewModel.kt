package com.example.hideandseek.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.example.hideandseek.data.datasource.local.TrapData
import com.example.hideandseek.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WatchFragmentViewModel @Inject constructor(
    locationRepository: LocationRepository,
    private val trapRepository: TrapRepository,
    private val userRepository: UserRepository,
    private val mapRepository: MapRepository,
) : ViewModel() {
    val allLocationsLive = locationRepository.allLocations.asLiveData()
    val allTrapsLive = trapRepository.allTraps.asLiveData()
    val userLive = userRepository.allUsers.asLiveData()

    private val _map = MutableLiveData<Bitmap>()
    val map: LiveData<Bitmap> = _map

    fun setMap(p0: Bitmap) {
        _map.value = p0
    }

    fun postTrapRoom(isMine: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            Log.d("USER_TRAP", userRepository.getLatest().toString())
            val nowUser = userRepository.getLatest()
            val trap = TrapData(0, nowUser.latitude, nowUser.longitude, nowUser.altitude, isMine)
            trapRepository.insert(trap)
        }
    }

    suspend fun fetchMap(url: String): Bitmap {
        return mapRepository.fetchMap(url)
    }
}
