package deepankumarpn.github.io.eggchef.domain.usecase.identifier

import deepankumarpn.github.io.eggchef.domain.model.AppIdentifiers
import deepankumarpn.github.io.eggchef.domain.repository.IdentifierRepository
import javax.inject.Inject

class GetIdentifiersUseCase @Inject constructor(
    private val repository: IdentifierRepository
) {
    suspend operator fun invoke(): AppIdentifiers = repository.getIdentifiers()
}
