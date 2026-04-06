package main.account;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SettingsTest {

    @Test
    void defaultConstructor_setsDefaults() {
        Settings settings = new Settings();

        assertEquals(5, settings.getVolume());
        assertTrue(settings.isMusicEnabled());
        assertTrue(settings.isSoundEffectsEnabled());
    }

    @Test
    void parameterizedConstructor_storesValues() {
        Settings settings = new Settings(3, false, false);

        assertEquals(3, settings.getVolume());
        assertFalse(settings.isMusicEnabled());
        assertFalse(settings.isSoundEffectsEnabled());
    }

    @Test
    void setVolume_acceptsValidRange() {
        Settings settings = new Settings();

        settings.setVolume(0);
        assertEquals(0, settings.getVolume());

        settings.setVolume(10);
        assertEquals(10, settings.getVolume());

        settings.setVolume(7);
        assertEquals(7, settings.getVolume());
    }

    @Test
    void setVolume_ignoresBelowZero() {
        Settings settings = new Settings();
        settings.setVolume(5);

        settings.setVolume(-1);

        assertEquals(5, settings.getVolume()); // unchanged
    }

    @Test
    void setVolume_ignoresAboveTen() {
        Settings settings = new Settings();
        settings.setVolume(5);

        settings.setVolume(11);

        assertEquals(5, settings.getVolume()); // unchanged
    }

    @Test
    void setMusicEnabled_togglesCorrectly() {
        Settings settings = new Settings();

        settings.setMusicEnabled(false);
        assertFalse(settings.isMusicEnabled());

        settings.setMusicEnabled(true);
        assertTrue(settings.isMusicEnabled());
    }

    @Test
    void setSoundEffectsEnabled_togglesCorrectly() {
        Settings settings = new Settings();

        settings.setSoundEffectsEnabled(false);
        assertFalse(settings.isSoundEffectsEnabled());

        settings.setSoundEffectsEnabled(true);
        assertTrue(settings.isSoundEffectsEnabled());
    }
}
