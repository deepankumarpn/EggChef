package deepankumarpn.github.io.eggchef.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import deepankumarpn.github.io.eggchef.data.repository.AnalyticsRepositoryImpl
import deepankumarpn.github.io.eggchef.domain.repository.AnalyticsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        impl: AnalyticsRepositoryImpl
    ): AnalyticsRepository
}
