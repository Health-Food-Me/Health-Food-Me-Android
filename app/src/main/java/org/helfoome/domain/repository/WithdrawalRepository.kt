package org.helfoome.domain.repository

import org.helfoome.data.model.response.EmptyResponse

interface WithdrawalRepository {

    suspend fun withdrawal(userId: String): EmptyResponse
}
