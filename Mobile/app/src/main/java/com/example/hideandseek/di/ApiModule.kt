package com.example.hideandseek.di

import android.os.Build
import com.example.hideandseek.data.datasource.remote.Params
import com.example.hideandseek.data.datasource.remote.RestApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Singleton
    @Provides
    fun provideRestApi(): RestApi {
        // 10秒でタイムアウトとなるように設定
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        fun isEmulator(): Boolean {
            return Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86") ||
                    Build.MODEL.contains("sdk_phone_armv7") ||
                    Build.MANUFACTURER.contains("Genymotion") ||
                    (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                    Build.PRODUCT == "google_sdk" ||
                    Build.PRODUCT == "sdk_gphone_x86"
        }

        fun setBaseUrl(): String {
            return if (isEmulator()) {
                Params.BASE_URL_EMULATOR
            } else {
                Params.BASE_URL_REAL
            }
        }

        return Retrofit.Builder()
            .baseUrl(setBaseUrl())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(RestApi::class.java)
    }
}