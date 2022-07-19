package org.helfoome.data.repository

import org.helfoome.data.model.request.RequestProfileModify
import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseProfileModify
import org.helfoome.data.service.AuthService
import org.helfoome.domain.repository.ProfileModifyRepository
import javax.inject.Inject

class ProfileModifyRepositoryImpl @Inject constructor(private val authService: AuthService) : ProfileModifyRepository {
    override suspend fun modifyProfile(requestProfileModify: RequestProfileModify, userId: String): BaseResponse<ResponseProfileModify> {
        return authService.modifyProfile(requestProfileModify, userId)
    }
}
