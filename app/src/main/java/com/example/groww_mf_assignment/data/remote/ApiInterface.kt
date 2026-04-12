package com.example.groww_mf_assignment.data.remote

import com.example.groww_mf_assignment.data.remote.dto.FundDetailsResponseDto
import com.example.groww_mf_assignment.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MfApiService {
    @GET("mf/search")
    suspend fun searchFunds(
        @Query("q") query: String
    ): List<SearchResponseDto>

    @GET("mf/{schemeCode}")
    suspend fun getFundDetails(
        @Path("schemeCode") schemeCode: Int
    ): FundDetailsResponseDto

    @GET("mf")
    suspend fun getInitialFunds(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<SearchResponseDto>

    @GET("mf/{schemeCode}/latest")
    suspend fun getLatestNav(
        @Path("schemeCode") schemeCode: Int
    ): FundDetailsResponseDto
}