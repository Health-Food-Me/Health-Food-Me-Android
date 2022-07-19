package org.helfoome.data.service

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseAutoComplete
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("/restaurant/search/auto")
    suspend fun getSearchAutoComplete(@Query("query") query: String): BaseResponse<List<ResponseAutoComplete>>
}
