package com.example.p2_api.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BlueGrey80,
    secondary = BlueGrey60,
    tertiary = Amber40,
    background = BackgroundDark,
    surface = BlueGrey20,
    onPrimary = BlueGrey10,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = BlueGrey90,
    onSurface = BlueGrey90
)

private val LightColorScheme = lightColorScheme(
    primary = BlueGrey40,
    secondary = BlueGrey60,
    tertiary = Amber40,
    background = BackgroundLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = BlueGrey20,
    onSurface = BlueGrey20
)

@Composable
fun P2_apiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Material You) desactivado por defecto: preferimos
    // nuestra paleta azul-grisácea fija en vez del color que tome del
    // fondo de pantalla del usuario.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
