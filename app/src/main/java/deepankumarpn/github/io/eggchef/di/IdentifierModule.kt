package deepankumarpn.github.io.eggchef.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import deepankumarpn.github.io.eggchef.data.repository.IdentifierRepositoryImpl
import deepankumarpn.github.io.eggchef.domain.repository.IdentifierRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IdentifierModule {

    @Binds
    @Singleton
    abstract fun bindIdentifierRepository(
        impl: IdentifierRepositoryImpl
    ): IdentifierRepository
}
