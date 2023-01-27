package com.example.hideandseek.data.datasource.remote

import com.squareup.moshi.Json

class PostData {
    data class PostSpacetime(
        @Json(name = "time") val time: String,
        @Json(name = "latitude") val latitude: Double,
        @Json(name = "longitude") val longitude: Double,
        @Json(name = "altitude") val altitude: Double,
        @Json(name = "obj_id") val objId: Int,
    )
}
