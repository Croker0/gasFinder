package com.example.gasfinder.ui.theme

import androidx.compose.ui.graphics.Color

// Палитра приложения: зелёный (заправка/экология) + янтарный акцент (топливо).
val GreenDark = Color(0xFF0F5132)
val Green = Color(0xFF1B7F4B)
val GreenLight = Color(0xFF4CAF7D)
val Amber = Color(0xFFEF8A17)
val AmberLight = Color(0xFFFFC46B)

val SurfaceLight = Color(0xFFF6F7F4)
val OnSurfaceLight = Color(0xFF1A1C19)
val OutlineLight = Color(0xFFC9CFC6)

val SurfaceDark = Color(0xFF12140F)
val OnSurfaceDark = Color(0xFFE2E3DD)
val OutlineDark = Color(0xFF44483F)

// Цвета брендов АЗС — используются для «шапки» карточки, чтобы станции
// различались визуально без загрузки логотипов.
val BrandColors = mapOf(
    "Лукойл" to Color(0xFFD2232A),
    "Газпромнефть" to Color(0xFF0072BC),
    "Роснефть" to Color(0xFFF2B705),
    "Татнефть" to Color(0xFF008C46),
    "Башнефть" to Color(0xFFF57C00),
    "Shell" to Color(0xFFDD1D21),
    "Neste" to Color(0xFF00A651),
    "Трасса" to Color(0xFF5E6B7A),
    "Моя АЗС" to Color(0xFF7E57C2)
)

/** Цвет бренда с разумным значением по умолчанию. */
fun brandColor(brand: String): Color = BrandColors[brand] ?: Green
