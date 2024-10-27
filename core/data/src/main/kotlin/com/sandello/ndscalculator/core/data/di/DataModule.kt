package com.sandello.ndscalculator.core.data.di

import com.sandello.ndscalculator.core.data.repository.OfflineSettingsRepository
import com.sandello.ndscalculator.core.data.repository.SettingsRepository
import com.sandello.vatcalculator.VatCalculator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsSettingsDataRepository(
        settingsRepository: OfflineSettingsRepository,
    ): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideVatCalculator(): VatCalculator = VatCalculator(rate = 20.0)
    }
}
