package merail.tools.info.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import merail.tools.info.api.IDeviceDataInformer
import merail.tools.info.api.IDisplayDataInformer
import merail.tools.info.api.INetworkDataInformer
import merail.tools.info.api.IOsDataInformer
import merail.tools.info.impl.repository.DeviceDataInformer
import merail.tools.info.impl.repository.DisplayDataInformer
import merail.tools.info.impl.repository.NetworkDataInformer
import merail.tools.info.impl.repository.OsDataInformer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface InfoModule {

    @Singleton
    @Binds
    fun bindDeviceDataInformer(
        deviceDataInformer: DeviceDataInformer,
    ): IDeviceDataInformer

    @Singleton
    @Binds
    fun bindDisplayDataInformer(
        displayDataInformer: DisplayDataInformer,
    ): IDisplayDataInformer

    @Singleton
    @Binds
    fun bindNetworkDataInformer(
        networkDataInformer: NetworkDataInformer,
    ): INetworkDataInformer

    @Singleton
    @Binds
    fun bindOsDataInformer(
        osDataInformer: OsDataInformer,
    ): IOsDataInformer
}