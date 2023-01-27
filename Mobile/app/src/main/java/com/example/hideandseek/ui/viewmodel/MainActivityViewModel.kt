package com.example.hideandseek.ui.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.hideandseek.data.datasource.remote.PostData
import com.example.hideandseek.data.datasource.remote.ResponseData
import com.example.hideandseek.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val trapRepository: TrapRepository,
    private val userRepository: UserRepository,
    private val apiRepository: ApiRepository,
) : ViewModel() {

    lateinit var relativeTime: LocalTime

    // relativeTimeの初期値（アプリを起動したときのLocalTime）をセットする
    fun setUpRelativeTime(nowTime: LocalTime) {
        relativeTime = nowTime
    }

    fun calculateRelativeTime(gap: Long) {
        relativeTime = relativeTime.minusNanos(gap)?.plusSeconds(1)!!
    }

    // 特殊相対性理論によりずれを計算する
    fun calculateGap(location: Location): Long {
        Log.d("GAP", "speed: ${location.speed}, calc: ${(1000000000 * (1 - sqrt(1 - (location.speed / 10).pow(2)))).roundToInt().toLong()}")
        return (1000000000 * (1 - sqrt(1 - (location.speed / 10).pow(2)))).roundToInt().toLong()
    }

    // ActivityからrelativeTimeとlocationを受け取り、Roomデータベースにuserデータとして送信
    fun insertUser(relativeTime: LocalTime, location: Location) = viewModelScope.launch {
        val user =
            com.example.hideandseek.data.datasource.local.UserData(0, relativeTime.toString().substring(0, 8), location.latitude, location.longitude, location.altitude)
        withContext(Dispatchers.IO) {
            userRepository.insert(user)
        }
    }

    private fun insertLocationAll(relativeTime: LocalTime, response: List<ResponseData.ResponseGetSpacetime>) = viewModelScope.launch {
        for (i in response.indices) {
            val user =
                com.example.hideandseek.data.datasource.local.LocationData(0, relativeTime.toString().substring(0, 8), response[i].latitude, response[i].longitude, response[i].altitude, response[i].objId)
            withContext(Dispatchers.IO) {
                locationRepository.insert(user)
            }
        }
    }

    fun postSpacetime(relativeTime: LocalTime, location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = PostData.PostSpacetime(relativeTime.toString().substring(0, 8), location.latitude, location.longitude, location.altitude, 0)
                val response = apiRepository.postSpacetime(request)
                if (response.isSuccessful) {
                    Log.d("POST_TEST", "${response}\n${response.body()}")
                } else {
                    Log.d("POST_TEST", "$response")
                }
            } catch (e: java.lang.Exception) {
                Log.d("POST_TEST", "$e")
            }
        }
    }

    fun getSpacetime(relativeTime: LocalTime) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiRepository.getSpacetime(relativeTime.toString().substring(0, 8))
                if (response.isSuccessful) {
                    Log.d("GET_TEST", "${response}\n${response.body()}")
                    response.body()?.let { insertLocationAll(relativeTime, it) }
                } else {
                    Log.d("GET_TEST", "$response")
                }
            } catch (e: java.lang.Exception) {
                Log.d("GET_TEST", "$e")
            }
        }
    }

    // Locationデータベースのデータを全消去
    fun deleteAllLocation() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            locationRepository.deleteAll()
        }
    }

    fun deleteAllUser() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userRepository.deleteAll()
        }
    }

    fun deleteAllTrap() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            trapRepository.deleteAll()
        }
    }
}
