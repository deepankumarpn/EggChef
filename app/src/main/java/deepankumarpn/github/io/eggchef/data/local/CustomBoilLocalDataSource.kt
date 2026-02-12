package deepankumarpn.github.io.eggchef.data.local

import deepankumarpn.github.io.eggchef.domain.model.BoilItem
import deepankumarpn.github.io.eggchef.utils.PrefKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomBoilLocalDataSource @Inject constructor(
    private val dataStorePreference: DataStorePreference,
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun getCustomBoilItems(): List<BoilItem> = withContext(dispatcher) {
        dataStorePreference.getList<BoilItem>(PrefKeys.KEY_CUSTOM_BOIL_LIST)
    }

    suspend fun saveCustomBoilItem(item: BoilItem) = withContext(dispatcher) {
        val currentItems = getCustomBoilItems().toMutableList()
        currentItems.add(item)
        dataStorePreference.putList(PrefKeys.KEY_CUSTOM_BOIL_LIST, currentItems)
    }

    suspend fun deleteCustomBoilItem(customIndex: Int) = withContext(dispatcher) {
        val currentItems = getCustomBoilItems().toMutableList()
        if (customIndex in currentItems.indices) {
            currentItems.removeAt(customIndex)
            dataStorePreference.putList(PrefKeys.KEY_CUSTOM_BOIL_LIST, currentItems)
        }
    }
}
