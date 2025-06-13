package com.sandello.ndscalculator.core.data.di

import com.sandello.ndscalculator.core.data.repository.OfflineUserPreferencesRepository
import com.sandello.ndscalculator.core.data.repository.UserPreferencesRepository
import com.sandello.vatcalculator.VatCalculator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.math.BigDecimal
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsSettingsDataRepository(
        settingsRepository: OfflineUserPreferencesRepository,
    ): UserPreferencesRepository

    companion object {
        @Provides
        @Singleton
        fun provideVatCalculator(): VatCalculator = VatCalculator(rate = BigDecimal.ZERO)
    }
}
