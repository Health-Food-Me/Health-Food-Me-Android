package org.helfoome.data.service

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseAutoComplete
import org.helfoome.data.model.response.ResponseSearchCard
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("/restaurant/search/auto")
    suspend fun getSearchAutoComplete(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("query") query: String
    ): BaseResponse<List<ResponseAutoComplete>>

    @GET("/restaurant/search/card")
    suspend fun getSearchRestaurantCard(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("keyword") keyword: String
    ): BaseResponse<List<ResponseSearchCard>>

    @GET("/restaurant/search/category")
    suspend fun getSearchCategoryCard(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("category") category: String
    ): BaseResponse<List<ResponseSearchCard>>
}
