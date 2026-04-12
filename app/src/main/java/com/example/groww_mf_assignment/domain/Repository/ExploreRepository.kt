package com.example.groww_mf_assignment.domain.Repository

import android.util.Log
import com.example.groww_mf_assignment.Resource_Class
import com.example.groww_mf_assignment.data.local.ExploreCacheEntity
import com.example.groww_mf_assignment.data.local.MfDao
import com.example.groww_mf_assignment.data.remote.MfApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepository @Inject constructor(
    private val api: MfApiService,
    private val dao: MfDao
) {
    fun getCategoryFunds(categoryName: String, searchQuery: String): Flow<Resource_Class<List<ExploreCacheEntity>>> = flow {
        emit(Resource_Class.Loading())


        val localData = dao.getExploreCacheByCategory(categoryName)
        if (localData.isNotEmpty()) {
            emit(Resource_Class.Success(localData))
        }


        try {
            val remoteData = api.searchFunds(searchQuery)
            val top4 = remoteData.take(4)


            val entitiesToCache = coroutineScope {
                top4.map { dto ->
                    async {
                        var navValue: String? = null
                        try {

                            val navResponse = api.getLatestNav(dto.schemeCode)
                            navValue = navResponse.data.firstOrNull()?.nav
                        } catch (e: Exception) {
                        }

                        ExploreCacheEntity(
                            schemeCode = dto.schemeCode,
                            schemeName = dto.schemeName,
                            category = categoryName,
                            latestNav = navValue
                        )
                    }
                }.awaitAll()
            }
            dao.deleteExploreCacheByCategory(categoryName)

            dao.insertExploreCache(entitiesToCache)
            emit(Resource_Class.Success(dao.getExploreCacheByCategory(categoryName)))

        } catch (e: IOException) {

            if (localData.isEmpty()) {
                emit(Resource_Class.Error("Couldn't reach server. Check your internet connection."))
            }
        } catch (e: HttpException) {

            if (localData.isEmpty()) {
                emit(Resource_Class.Error("An unexpected error occurred."))
            }
        }
    }
}