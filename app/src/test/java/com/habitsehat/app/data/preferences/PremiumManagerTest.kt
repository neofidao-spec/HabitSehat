package com.habitsehat.app.data.preferences

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class PremiumManagerTest {
    @Mock private lateinit var settingsManager: SettingsManager
    private lateinit var premiumManager: PremiumManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        premiumManager = PremiumManager(settingsManager)
    }

    @Test
    fun canUseTheme_freeTheme_shouldReturnTrue() = runBlocking {
        val result = premiumManager.canUseTheme("free_theme", isPremiumNow = false)
        assertTrue(result)
    }

    @Test
    fun canUseTheme_premiumTheme_withPremium_shouldReturnTrue() = runBlocking {
        val result = premiumManager.canUseTheme("premium_theme", isPremiumNow = true)
        assertTrue(result)
    }

    @Test
    fun canUseTheme_premiumTheme_withoutPremium_shouldReturnFalse() = runBlocking {
        val result = premiumManager.canUseTheme("premium_theme", isPremiumNow = false)
        assertFalse(result)
    }

    @Test
    fun unlockPremium_shouldCallSettingsManager() = runBlocking {
        premiumManager.unlockPremium()
        org.mockito.kotlin.verify(settingsManager).setPremium(true)
    }

    @Test
    fun lockPremium_shouldCallSettingsManager() = runBlocking {
        premiumManager.lockPremium()
        org.mockito.kotlin.verify(settingsManager).setPremium(false)
    }

    @Test
    fun features_shouldContain7Items() {
        assertEquals(7, PremiumManager.FEATURES.size)
    }

    private fun assertEquals(expected: Int, actual: Int) {
        if (expected != actual) throw AssertionError("Expected $expected but was $actual")
    }
}