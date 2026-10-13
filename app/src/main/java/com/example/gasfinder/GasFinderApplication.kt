package com.example.gasfinder

import android.app.Application
import android.content.Context
import com.example.gasfinder.data.PreferencesRepository
import org.osmdroid.config.Configuration

/**
 * Точка входа приложения.
 *
 * Здесь один раз настраивается osmdroid: библиотека обязана знать,
 * какой User-Agent отправлять при запросе тайлов OpenStreetMap
 * (этого требуют правила использования тайл-сервера), и куда складывать кэш.
 */
class GasFinderApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Кэш тайлов и настройки osmdroid держим в приватном хранилище приложения:
        // так не нужны разрешения на внешнюю память и данные не «протекают» наружу.
        Configuration.getInstance().load(
            this,
            getSharedPreferences(PreferencesRepository.PREFS_NAME, Context.MODE_PRIVATE)
        )
        // User-Agent обязателен: правила использования тайл-сервера OpenStreetMap
        // запрещают обезличенные клиенты и блокируют их (ответ 403 «Access blocked»).
        // UA должен называть приложение, платформу и назначение — иначе тайлы
        // не загрузятся, хотя библиотека работает корректно.
        Configuration.getInstance().userAgentValue =
            "GasFinder/1.0 (Android; college student project; $packageName)"
    }
}
