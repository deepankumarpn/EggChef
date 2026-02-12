package deepankumarpn.github.io.eggchef.data.repository

import deepankumarpn.github.io.eggchef.data.local.IdentifierLocalDataSource
import deepankumarpn.github.io.eggchef.domain.model.AppIdentifiers
import deepankumarpn.github.io.eggchef.domain.repository.IdentifierRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdentifierRepositoryImpl @Inject constructor(
    private val localDataSource: IdentifierLocalDataSource,
    private val scope: CoroutineScope
) : IdentifierRepository {

    override suspend fun getIdentifiers(): AppIdentifiers = localDataSource.getAllIdentifiers()

    override fun initSession() {
        scope.launch { localDataSource.initializeNewLaunchSession() }
    }
}
