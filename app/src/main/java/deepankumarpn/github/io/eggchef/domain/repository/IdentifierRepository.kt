package deepankumarpn.github.io.eggchef.domain.repository

import deepankumarpn.github.io.eggchef.domain.model.AppIdentifiers

interface IdentifierRepository {
    suspend fun getIdentifiers(): AppIdentifiers
    fun initSession()
}
