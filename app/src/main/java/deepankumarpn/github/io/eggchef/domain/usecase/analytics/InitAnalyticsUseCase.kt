package deepankumarpn.github.io.eggchef.domain.usecase.analytics

import deepankumarpn.github.io.eggchef.domain.repository.AnalyticsRepository
import javax.inject.Inject

class InitAnalyticsUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    operator fun invoke() {
        repository.initialize()
    }
}
