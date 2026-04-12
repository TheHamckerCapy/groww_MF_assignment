package com.example.groww_mf_assignment.domain.Repository

import android.util.Log
import com.example.groww_mf_assignment.Resource_Class
import com.example.groww_mf_assignment.data.remote.MfApiService
import com.example.groww_mf_assignment.data.remote.dto.FundDetailsResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FundDetailsRepository @Inject constructor(
    private val api: MfApiService
) {
    fun getFundDetails(schemeCode: Int): Flow<Resource_Class<FundDetailsResponseDto>> = flow {
        emit(Resource_Class.Loading(true))
        try {
            val response = api.getFundDetails(schemeCode)
            Log.d("FundDetailsRepository", "Response: $response")
            emit(Resource_Class.Success(response))
        } catch (e: IOException) {
            emit(Resource_Class.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: HttpException) {
            emit(Resource_Class.Error("An unexpected error occurred."))
        }
    }
}