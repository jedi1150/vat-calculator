package com.sandello.ndscalculator.feature.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sandello.ndscalculator.feature.settings.R
import com.sandello.ndscalculator.feature.settings.SettingsUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsLanguageDialog(
    onDismiss: () -> Unit,
    settingsUiState: SettingsUiState,
    onChangeLocale: (Locale) -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        var selectedLocale by rememberSaveable {
            mutableStateOf(settingsUiState.locale)
        }
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .selectableGroup(),
            ) {
                SettingsDialogTitle(stringResource(R.string.settings_language))
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = false)
                        .verticalScroll(state = rememberScrollState()),
                ) {
                    settingsUiState.availableLocales.forEach { locale ->
                        SettingsDialogThemeChooserRow(
                            text = localeOptionLabel(locale),
                            selected = selectedLocale == locale,
                            onClick = { selectedLocale = locale },
                        )
                    }
                }
                SettingsDialogActions(
                    onDismiss = onDismiss,
                    onConfirm = {
                        onDismiss()
                        onChangeLocale(selectedLocale)
                    },
                )
            }
        }
    }
}

@Composable
private fun localeOptionLabel(locale: Locale): String {
    if (locale == Locale.ROOT) {
        return stringResource(id = R.string.locale_system)
    }
    return locale.getDisplayLanguage(locale).replaceFirstChar { letter ->
        if (letter.isLowerCase()) letter.titlecase(locale) else letter.toString()
    }
}
