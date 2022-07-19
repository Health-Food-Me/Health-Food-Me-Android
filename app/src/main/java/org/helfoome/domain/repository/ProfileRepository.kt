package org.helfoome.domain.repository

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseProfile

interface ProfileRepository {
    suspend fun getProfile(userId: String): BaseResponse<ResponseProfile>
}
