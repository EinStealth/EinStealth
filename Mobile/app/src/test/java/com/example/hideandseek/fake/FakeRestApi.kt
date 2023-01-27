package com.example.hideandseek.fake

import com.example.hideandseek.data.datasource.remote.PostData
import com.example.hideandseek.data.datasource.remote.ResponseData
import com.example.hideandseek.data.datasource.remote.RestApi
import retrofit2.Response

class FakeRestApi : RestApi {
    override suspend fun getTest(): Response<ResponseData.ResponseGetTest> {
        return FakeDataSource.fakeResponseGetTest
    }

    override suspend fun postStatus(id: Int, status: Int): Response<ResponseData.ResponsePost> {
        return FakeDataSource.fakeResponsePost
    }

    override suspend fun getSpacetime(time: String): Response<List<ResponseData.ResponseGetSpacetime>> {
        return FakeDataSource.fakeResponseGetSpacetime
    }

    override suspend fun postSpacetime(request: PostData.PostSpacetime): Response<ResponseData.ResponsePost> {
        return FakeDataSource.fakeResponsePost
    }
}
