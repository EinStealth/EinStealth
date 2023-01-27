package com.example.hideandseek.data.datasource.remote

import com.squareup.moshi.Json

class ResponseData {
    data class ResponseGetTest(
        @Json(name = "message") val message: String,
    )

    data class ResponsePost(
        @Json(name = "message") val message: String,
    )

    data class ResponseGetSpacetime(
        @Json(name = "latitude") val latitude: Double,
        @Json(name = "longitude") val longitude: Double,
        @Json(name = "altitude") val altitude: Double,
        @Json(name = "obj_id") val objId: Int,
    )
}
