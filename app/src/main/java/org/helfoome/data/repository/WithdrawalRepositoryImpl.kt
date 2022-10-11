package org.helfoome.data.repository

import org.helfoome.data.model.response.EmptyResponse
import org.helfoome.data.service.AuthService
import org.helfoome.domain.repository.WithdrawalRepository
import javax.inject.Inject

class WithdrawalRepositoryImpl @Inject constructor(private val authService: AuthService) :
    WithdrawalRepository {
    override suspend fun withdrawal(userId: String): EmptyResponse {
        return authService.withdrawal(userId)
    }
}
