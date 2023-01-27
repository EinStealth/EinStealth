package com.example.hideandseek.fake

import com.example.hideandseek.data.repository.ApiRepositoryImpl
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ApiRepositoryTest {
    @Test
    fun apiRepository_getTest_verifyFakeResponseGetTest() = runTest {
        val repository = ApiRepositoryImpl(
            FakeRestApi(),
        )
        assertEquals(FakeDataSource.fakeResponseGetTest, repository.getTest())
    }

    @Test
    fun apiRepository_postStatus_verifyFakeResponsePost() = runTest {
        val repository = ApiRepositoryImpl(
            FakeRestApi(),
        )
        assertEquals(FakeDataSource.fakeResponsePost, repository.postStatus(0, 0))
    }

    @Test
    fun apiRepository_getSpacetimes_verifyFakeResponseGetSpacetime() = runTest {
        val repository = ApiRepositoryImpl(
            FakeRestApi(),
        )
        assertEquals(FakeDataSource.fakeResponseGetSpacetime, repository.getSpacetime("00:00:00"))
    }

    @Test
    fun apiRepository_postSpacetime_verifyFakeResponsePost() = runTest {
        val repository = ApiRepositoryImpl(
            FakeRestApi(),
        )
        assertEquals(FakeDataSource.fakeResponsePost, repository.postSpacetime(FakeDataSource.fakePostSpacetime))
    }
}
