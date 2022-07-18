package org.helfoome.domain.repository

import org.helfoome.data.model.request.RequestProfileModify
import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseProfileModify

interface ProfileModifyRepository {
    suspend fun modifyProfile(requestProfileModify: RequestProfileModify, userId: String): BaseResponse<ResponseProfileModify>
}