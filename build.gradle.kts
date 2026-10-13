// Корневой build-скрипт. Плагины объявляются здесь и применяются в модуле :app.
plugins {
    alias(libs.plugins.android.application) apply false
    // org.jetbrains.kotlin.android не подключаем: с AGP 9 поддержка Kotlin встроена.
    alias(libs.plugins.kotlin.compose) apply false
}
