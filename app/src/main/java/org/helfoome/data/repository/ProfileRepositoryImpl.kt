package org.helfoome.data.repository

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseProfile
import org.helfoome.data.service.AuthService
import org.helfoome.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val authService: AuthService) : ProfileRepository {

    override suspend fun getProfile(userId: String): BaseResponse<ResponseProfile> {
        return authService.getProfile(userId)
    }
}
