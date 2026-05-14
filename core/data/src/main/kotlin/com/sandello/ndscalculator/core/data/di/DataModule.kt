package com.sandello.ndscalculator.core.data.di

import com.sandello.ndscalculator.core.data.repository.OfflineUserPreferencesRepository
import com.sandello.ndscalculator.core.data.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsSettingsDataRepository(
        settingsRepository: OfflineUserPreferencesRepository,
    ): UserPreferencesRepository

}
