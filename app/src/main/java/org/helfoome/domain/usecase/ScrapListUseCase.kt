package org.helfoome.domain.usecase

import org.helfoome.domain.entity.ScrapInfo
import org.helfoome.domain.repository.ScrapRepository
import javax.inject.Inject

class ScrapListUseCase @Inject constructor(private val repository: ScrapRepository) {

    suspend fun execute(userId: String): Result<List<ScrapInfo>> {
        return repository.getScrapList(userId)
    }
}
