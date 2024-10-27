package com.sandello.ndscalculator.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sandello.ndscalculator.core.datastore.Settings
import com.sandello.ndscalculator.core.datastore.SettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesSettingsDataStore(
        @ApplicationContext context: Context,
        settingsSerializer: SettingsSerializer,
    ): DataStore<Settings> =
        DataStoreFactory.create(serializer = settingsSerializer) {
            context.dataStoreFile("settings.pb")
        }
}
