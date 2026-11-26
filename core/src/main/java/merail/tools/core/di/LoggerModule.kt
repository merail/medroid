package merail.tools.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import merail.tools.core.ILogger
import merail.tools.core.Logger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface LoggerModule {

    @Binds
    @Singleton
    fun bindLogger(logger: Logger): ILogger
}
