package com.example.hideandseek.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.hideandseek.data.datasource.local.TrapData
import com.example.hideandseek.data.datasource.local.UserData
import com.example.hideandseek.data.datasource.remote.PostData
import com.example.hideandseek.data.repository.ApiRepository
import com.example.hideandseek.data.repository.TrapRepository
import com.example.hideandseek.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BeTrappedFragmentViewModel @Inject constructor(
    private val trapRepository: TrapRepository,
    private val userRepository: UserRepository,
    private val apiRepository: ApiRepository,
) : ViewModel() {
    val userLive = userRepository.allUsers.asLiveData()

    suspend fun getNowUser(): UserData {
        return userRepository.getLatest()
    }

    fun postTrapRoom(isMine: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            Log.d("USER_TRAP", userRepository.getLatest().toString())
            val nowUser = userRepository.getLatest()
            val trap = TrapData(0, nowUser.latitude, nowUser.longitude, nowUser.altitude, isMine)
            trapRepository.insert(trap)
        }
    }

    private val _skillTime = MutableLiveData<String>()
    val skillTime: LiveData<String> = _skillTime

    fun setSkillTime() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val nowUser = userRepository.getLatest()
            _skillTime.value = nowUser.relativeTime
        }
    }

    fun setSkillTimeInit(skillTime: String) {
        _skillTime.value = skillTime
    }

    private val _isOverLimitTime = MutableLiveData<Boolean>()
    val isOverLimitTime: LiveData<Boolean> = _isOverLimitTime

    // 相対時間が制限時間を超えてたらtrueを返す
    fun compareTime(relativeTime: String, limitTime: String) {
        _isOverLimitTime.value = relativeTime.substring(0, 2) == limitTime.substring(0, 2) && relativeTime.substring(3, 5) == limitTime.substring(3, 5) && relativeTime.substring(6) > limitTime.substring(6)
    }

    private val _isOverSkillTime = MutableLiveData<Boolean>()
    val isOverSkillTime: LiveData<Boolean> = _isOverSkillTime

    fun compareSkillTime(relativeTime: String, skillTime: String) {
        Log.d("CompareSkillTime", "relative: $relativeTime, skill: $skillTime")
        if (relativeTime.substring(6, 8) == skillTime.substring(6, 8)) {
            _isOverSkillTime.value = relativeTime != skillTime
        }
    }

    private val _isOverTrapTime = MutableLiveData<Boolean>()
    val isOverTrapTime: LiveData<Boolean> = _isOverTrapTime

    fun compareTrapTime(relativeTime: String, trapTime: String) {
        Log.d("CompareTrapTime", "relative: $relativeTime, trap: $trapTime")
        if (relativeTime.substring(6, 8) == trapTime.substring(6, 8)) {
            _isOverTrapTime.value = relativeTime != trapTime
        }
    }

    fun howProgressSkillTime(relativeTime: String, skillTime: String): Int {
        Log.d("HowProgress", ((60 + relativeTime.substring(6).toInt() - skillTime.substring(6).toInt()) % 60).toString())
        return if (relativeTime.substring(6).toInt() < skillTime.substring(6).toInt()) {
            (60 + relativeTime.substring(6).toInt() - skillTime.substring(6).toInt()) % 60
        } else {
            relativeTime.substring(6).toInt() - skillTime.substring(6).toInt()
        }
    }

    fun howProgressTrapTime(relativeTime: String, trapTime: String): Int {
        Log.d("HowProgressTrap", ((60 + relativeTime.substring(6).toInt() - trapTime.substring(6).toInt()) % 60).toString())
        return if (relativeTime.substring(6).toInt() < trapTime.substring(6).toInt()) {
            (60 + relativeTime.substring(6).toInt() - trapTime.substring(6).toInt()) % 60
        } else {
            relativeTime.substring(6).toInt() - trapTime.substring(6).toInt()
        }
    }

    fun setIsOverSkillTime(p0: Boolean) {
        _isOverSkillTime.value = p0
    }

    fun postTrapSpacetime() {
        viewModelScope.launch(Dispatchers.IO) {
            val nowUser = userRepository.getLatest()
            try {
                val request = PostData.PostSpacetime(nowUser.relativeTime.substring(0, 7) + "0", nowUser.latitude, nowUser.longitude, nowUser.altitude, 1)
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
}