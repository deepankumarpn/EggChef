package deepankumarpn.github.io.eggchef.domain.usecase.identifier

import deepankumarpn.github.io.eggchef.domain.repository.IdentifierRepository
import javax.inject.Inject

class InitSessionUseCase @Inject constructor(
    private val repository: IdentifierRepository
) {
    operator fun invoke() {
        repository.initSession()
    }
}
