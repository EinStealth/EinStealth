package com.example.hideandseek.data.repository

import com.example.hideandseek.data.datasource.remote.PostData
import com.example.hideandseek.data.datasource.remote.ResponseData
import com.example.hideandseek.data.datasource.remote.RestApi
import retrofit2.Response
import javax.inject.Inject

interface ApiRepository {
    suspend fun getTest(): Response<ResponseData.ResponseGetTest>

    suspend fun postStatus(id: Int, status: Int): Response<ResponseData.ResponsePost>

    suspend fun getSpacetime(time: String): Response<List<ResponseData.ResponseGetSpacetime>>

    suspend fun postSpacetime(request: PostData.PostSpacetime): Response<ResponseData.ResponsePost>
}

class ApiRepositoryImpl @Inject constructor(
    private val restApiService: RestApi,
) : ApiRepository {
    override suspend fun getTest(): Response<ResponseData.ResponseGetTest> =
        restApiService.getTest()

    override suspend fun postStatus(id: Int, status: Int): Response<ResponseData.ResponsePost> =
        restApiService.postStatus(id, status)

    override suspend fun getSpacetime(time: String): Response<List<ResponseData.ResponseGetSpacetime>> =
        restApiService.getSpacetime(time)

    override suspend fun postSpacetime(request: PostData.PostSpacetime): Response<ResponseData.ResponsePost> =
        restApiService.postSpacetime(request)
}
