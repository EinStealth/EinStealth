package com.example.hideandseek.fake

import com.example.hideandseek.data.datasource.remote.PostData
import com.example.hideandseek.data.datasource.remote.ResponseData
import retrofit2.Response

object FakeDataSource {
    private const val latitudeOne = 0.0
    private const val longitudeOne = 0.0
    private const val altitudeOne = 0.0
    private const val objIdOne = 0

    private const val latitudeSecond = 0.3
    private const val longitudeSecond = 0.3
    private const val altitudeSecond = 0.0
    private const val objIdSecond = 0

    private val fakeGetSpacetimeList = listOf(
        ResponseData.ResponseGetSpacetime(latitudeOne, longitudeOne, altitudeOne, objIdOne),
        ResponseData.ResponseGetSpacetime(latitudeSecond, longitudeSecond, altitudeSecond, objIdSecond),
    )

    val fakeResponseGetSpacetime: Response<List<ResponseData.ResponseGetSpacetime>> = Response.success(
        fakeGetSpacetimeList,
    )

    private val fakeGetTest = ResponseData.ResponseGetTest("test")

    val fakeResponseGetTest: Response<ResponseData.ResponseGetTest> = Response.success(
        fakeGetTest,
    )

    private val fakePost = ResponseData.ResponsePost("local test success")

    val fakeResponsePost: Response<ResponseData.ResponsePost> = Response.success(
        fakePost,
    )

    val fakePostSpacetime = PostData.PostSpacetime(
        "00:00:00",
        latitudeOne,
        longitudeOne,
        altitudeOne,
        objIdOne,
    )
}
