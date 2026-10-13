package com.example.gasfinder.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit-тесты чистой логики: расчёт расстояния и форматирование.
 * Запускаются на JVM, без эмулятора: ./gradlew test
 */
class GeoTest {

    @Test
    fun `расстояние до самой себя равно нулю`() {
        val d = Geo.distanceKm(56.8389, 60.6057, 56.8389, 60.6057)
        assertEquals(0.0, d, 0.0001)
    }

    @Test
    fun `один километр на север считается верно`() {
        // 0,009 градуса широты — приблизительно 1 км
        val d = Geo.distanceKm(56.8389, 60.6057, 56.8479, 60.6057)
        assertTrue("получилось $d км", d in 0.95..1.05)
    }

    @Test
    fun `расстояние симметрично`() {
        val a = Geo.distanceKm(56.8389, 60.6057, 56.9000, 60.7000)
        val b = Geo.distanceKm(56.9000, 60.7000, 56.8389, 60.6057)
        assertEquals(a, b, 0.0001)
    }

    @Test
    fun `метры форматируются без дробной части`() {
        assertEquals("850 м", Geo.formatDistance(0.85))
    }

    @Test
    fun `километры форматируются с запятой`() {
        assertEquals("3,4 км", Geo.formatDistance(3.42))
    }

    @Test
    fun `большие расстояния округляются`() {
        assertEquals("126 км", Geo.formatDistance(125.7))
    }

    @Test
    fun `цена форматируется с двумя знаками`() {
        assertEquals("58,40", Geo.formatPrice(58.4))
        assertEquals("92,05", Geo.formatPrice(92.05))
    }
}
