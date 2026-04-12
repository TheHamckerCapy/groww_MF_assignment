package com.example.groww_mf_assignment.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponseDto(
    @Json(name = "schemeCode") val schemeCode: Int,
    @Json(name = "schemeName") val schemeName: String
)

@JsonClass(generateAdapter = true)
data class FundDetailsResponseDto(
    @Json(name = "meta") val meta: MetaDto,
    @Json(name = "data") val data: List<NavDataDto>
)

@JsonClass(generateAdapter = true)
data class MetaDto(
    @Json(name = "fund_house") val fundHouse: String,
    @Json(name = "scheme_type") val schemeType: String,
    @Json(name = "scheme_category") val schemeCategory: String,
    @Json(name = "scheme_code") val schemeCode: Int,
    @Json(name = "scheme_name") val schemeName: String
)

@JsonClass(generateAdapter = true)
data class NavDataDto(
    @Json(name = "date") val date: String,
    @Json(name = "nav") val nav: String
)