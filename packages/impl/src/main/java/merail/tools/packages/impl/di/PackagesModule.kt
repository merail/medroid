package merail.tools.packages.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import merail.tools.packages.api.IPackagesRepository
import merail.tools.packages.impl.repository.PackagesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PackagesModule {

    @Singleton
    @Binds
    fun bindPackagesRepository(
        packagesRepository: PackagesRepository,
    ): IPackagesRepository
}