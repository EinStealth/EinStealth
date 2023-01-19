package com.example.hideandseek.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.example.hideandseek.data.datasource.local.LocationData
import com.example.hideandseek.data.datasource.local.TrapData
import com.example.hideandseek.data.datasource.local.UserData
import com.example.hideandseek.data.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchFragmentViewModel: ViewModel() {
    lateinit var allLocationsLive: LiveData<List<LocationData>>
    lateinit var allTrapsLive: LiveData<List<TrapData>>
    lateinit var userLive: LiveData<List<UserData>>
    private val repository = ApiRepository.instance

    fun setAllLocationsLive(context: Context) {
        allLocationsLive = LocationRepository(context).allLocations.asLiveData()
    }

    fun setAllTrapsLive(context: Context) {
        allTrapsLive = TrapRepository(context).allTraps.asLiveData()
    }

    fun setUserLive(context: Context) {
        userLive = UserRepository(context).allUsers.asLiveData()
    }

    private val _map = MutableLiveData<Bitmap>()
    val map: LiveData<Bitmap> = _map

    fun setMap(p0: Bitmap) {
        _map.value = p0
    }

    fun postTrapRoom(context: Context, isMine: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            Log.d("USER_TRAP", UserRepository(context).nowUser.toString())
            val nowUser = UserRepository(context).nowUser
            val trap = TrapData(0, nowUser.latitude, nowUser.longitude, nowUser.altitude, isMine)
            TrapRepository(context).insert(trap)
        }
    }

    suspend fun fetchMap(url: String): Bitmap {
        return MapRepository().fetchMap(url)
    }
}