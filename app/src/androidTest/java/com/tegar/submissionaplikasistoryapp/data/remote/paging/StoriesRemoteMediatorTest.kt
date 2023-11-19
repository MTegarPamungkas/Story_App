package com.tegar.submissionaplikasistoryapp.data.remote.paging

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tegar.submissionaplikasistoryapp.data.local.database.StoriesDatabase
import com.tegar.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseAdd
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseGetAll
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseLogin
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseRegister
import com.tegar.submissionaplikasistoryapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoriesRemoteMediatorTest {
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoriesDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoriesDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoriesRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun register(name: String, email: String, password: String): ResponseRegister {
        throw NotImplementedError("Not needed for this test")
    }

    override suspend fun login(email: String, password: String): ResponseLogin {
        throw NotImplementedError("Not needed for this test")
    }

    override suspend fun getAllStoriesWithPagging(page: Int, size: Int): ResponseGetAll {
        val items: MutableList<ListStoryItem> = arrayListOf()

        for (i in 0..100) {
            val story = ListStoryItem(
                "photoURL $i",
                "29102023 $i",
                "name $i",
                "Description $i",
                16.0,
                "id $i",
                15.0
            )
            items.add(story)
        }
        val listStory = items.subList((page - 1) * size, (page - 1) * size + size)
        return ResponseGetAll(listStory, error = false, message = "Success")
    }

    override suspend fun getAllStoriesWithLocation(size: Int): ResponseGetAll {
        throw NotImplementedError("Not needed for this test")
    }

    override suspend fun postStory(
        description: RequestBody,
        file: MultipartBody.Part,
        lat: Float?,
        lon: Float?,
    ): ResponseAdd {
        throw NotImplementedError("Not needed for this test")
    }
}