package com.rovenskyi.radio_lux_fm_lviv_streamer
import android.content.Context
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.CheckNetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCheckNetworkService(@ApplicationContext context: Context): CheckNetworkService {
        return CheckNetworkService(context)
    }
}
