package com.example.gasfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gasfinder.R

/**
 * Онбординг из трёх шагов. Показывается один раз — факт прохождения
 * сохраняется в SharedPreferences, поэтому при следующем запуске
 * пользователь сразу попадает на карту.
 */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onRequestPermission: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }

    // Собственные векторные иконки: id ресурсов, а не ImageVector
    val icons = listOf(R.drawable.ic_location, R.drawable.ic_location, R.drawable.ic_refresh)
    val titles = listOf(
        stringResource(R.string.onboarding_title_1),
        stringResource(R.string.onboarding_title_2),
        stringResource(R.string.onboarding_title_3)
    )
    val texts = listOf(
        stringResource(R.string.onboarding_text_1),
        stringResource(R.string.onboarding_text_2),
        stringResource(R.string.onboarding_text_3)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icons[step]),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(Modifier.height(28.dp))
        Text(
            text = titles[step],
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = texts[step],
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )

        Spacer(Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == step) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == step) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        )
                )
            }
        }

        Spacer(Modifier.height(36.dp))
        Button(
            onClick = {
                if (step < 2) {
                    step++
                } else {
                    onRequestPermission()
                    onFinish()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(if (step < 2) R.string.onboarding_next else R.string.onboarding_start))
        }

        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onFinish) {
                Text(stringResource(R.string.onboarding_skip))
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { onRequestPermission() }) {
                Text(stringResource(R.string.onboarding_grant))
            }
        }
    }
}
