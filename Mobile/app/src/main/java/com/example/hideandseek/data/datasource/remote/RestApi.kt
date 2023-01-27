package com.example.hideandseek.data.datasource.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RestApi {
    @GET("ping")
    suspend fun getTest(): Response<ResponseData.ResponseGetTest>

    @POST("api/v1/players/{id}/status/{status}")
    suspend fun postStatus(@Path("id") id: Int, @Path("status") status: Int): Response<ResponseData.ResponsePost>

    @GET("api/v1/spacetimes")
    suspend fun getSpacetime(@Query("time") time: String): Response<List<ResponseData.ResponseGetSpacetime>>

    @POST("api/v1/spacetimes")
    suspend fun postSpacetime(@Body request: PostData.PostSpacetime): Response<ResponseData.ResponsePost>
}
