package deepankumarpn.github.io.eggchef

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import deepankumarpn.github.io.eggchef.domain.usecase.analytics.InitAnalyticsUseCase
import deepankumarpn.github.io.eggchef.domain.usecase.identifier.InitSessionUseCase
import javax.inject.Inject

@HiltAndroidApp
class EggChefApplication : Application() {

    @Inject
    lateinit var initSessionUseCase: InitSessionUseCase

    @Inject
    lateinit var initAnalyticsUseCase: InitAnalyticsUseCase

    override fun onCreate() {
        super.onCreate()
        initSessionUseCase()
        initAnalyticsUseCase()
    }
}
