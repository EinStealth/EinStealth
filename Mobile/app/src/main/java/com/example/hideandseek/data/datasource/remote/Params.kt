package com.example.hideandseek.data.datasource.remote

class Params {
    companion object {
        // 実機でlocalhostに接続する場合(各PCでIPアドレスを変更して使用してください)
        const val BASE_URL_REAL = "http://100.65.117.208:8080/"

        // Emulatorでlocalhostに接続する場合
        const val BASE_URL_EMULATOR = "http://10.0.2.2:8080/"
    }
}
