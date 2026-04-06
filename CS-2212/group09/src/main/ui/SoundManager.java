package main.ui;

import main.account.Settings;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Manages all audio playback for KeyHunter including looping background music and one-shot sound effects.
 *
 * Volume is stored as a linear float in 0.0 to 1.0 and converted to decibels using a logarithmic
 * scale for perceptually uniform loudness. A single instance is created by KeyHunterApp and passed
 * to ScreenManager, which calls playMusic, stopMusic, and applySettings during transitions.
 *
 * @author Adam Porbanderwalla, Imad Tahir
 */
public class SoundManager {

    /** Root directory for audio assets relative to the project working directory. */
    private static final String AUDIO_BASE = "src/main/assets/audio/";

    private Clip musicClip;
    private String currentTrack;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    private float volume = 0.5f;

    /**
     * Starts playing the given audio file as looping background music.
     * Does nothing if the requested track is already playing. Called by ScreenManager on screen transitions.
     *
     * @param filename audio file name relative to src/main/assets/audio/
     */
    public void playMusic(String filename) {
        if (filename.equals(currentTrack) && musicClip != null && musicClip.isRunning()) return;
        stopMusic();
        if (!musicEnabled) return;
        try {
            File file = new File(AUDIO_BASE + filename);
            if (!file.exists()) {
                System.err.println("Audio file not found: " + filename);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            musicClip = AudioSystem.getClip();
            musicClip.open(ais);
            ais.close();
            currentTrack = filename;
            applyVolume(musicClip);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Failed to play music: " + filename + " - " + e.getMessage());
        }
    }

    /**
     * Stops and closes the current background music clip.
     * Called by ScreenManager.logout() and internally before starting a new track.
     */
    public void stopMusic() {
        if (musicClip != null) {
            if (musicClip.isRunning()) musicClip.stop();
            musicClip.close();
            musicClip = null;
            currentTrack = null;
        }
    }

    /**
     * Plays a sound effect once without interrupting background music.
     * The clip is automatically closed when playback ends. Called by game screens and button handlers.
     *
     * @param filename audio file name relative to src/main/assets/audio/
     */
    public void playSFX(String filename) {
        if (!sfxEnabled) return;
        try {
            File file = new File(AUDIO_BASE + filename);
            if (!file.exists()) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            ais.close();
            applyVolume(clip);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (Exception e) {
            System.err.println("Failed to play SFX: " + filename + " - " + e.getMessage());
        }
    }

    /**
     * Applies the current volume to a clip's MASTER_GAIN control using a logarithmic dB conversion.
     * Called by playMusic and playSFX after opening a clip, and by setVolume on the live music clip.
     *
     * @param clip the open Clip whose gain should be updated
     */
    private void applyVolume(Clip clip) {
        try {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // MASTER_GAIN is in decibels; linear interpolation sounds too quiet.
            // Convert 0.0-1.0 to a dB range using logarithmic scale.
            float dB;
            if (volume <= 0f) {
                dB = control.getMinimum();
            } else {
                dB = (float)(20.0 * Math.log10(volume));
                dB = Math.max(dB, control.getMinimum());
                dB = Math.min(dB, control.getMaximum());
            }
            control.setValue(dB);
        } catch (Exception ignored) {}
    }

    /**
     * Sets the playback volume from an integer level in the range 0 to 10.
     * Applies immediately to the running music clip. Called by applySettings.
     *
     * @param level integer volume level from 0 (silent) to 10 (maximum)
     */
    public void setVolume(int level) {
        this.volume = Math.max(0f, Math.min(1f, level / 10.0f));
        if (musicClip != null && musicClip.isOpen()) {
            applyVolume(musicClip);
        }
    }

    /**
     * Returns whether background music is currently enabled. Read by ScreenManager and SettingsPanel.
     *
     * @return true if music is enabled
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Enables or disables background music. Stops any playing clip immediately when disabled.
     * Called by applySettings and SettingsPanel.
     *
     * @param enabled true to allow music playback, false to stop and suppress it
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) stopMusic();
    }

    /**
     * Returns whether sound effect playback is currently enabled. Read by SettingsPanel.
     *
     * @return true if SFX are enabled
     */
    public boolean isSFXEnabled() {
        return sfxEnabled;
    }

    /**
     * Enables or disables sound effect playback. Called by applySettings and SettingsPanel.
     *
     * @param enabled true to allow SFX, false to suppress it
     */
    public void setSFXEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
    }

    /**
     * Applies volume, music, and SFX settings from a Settings object in one call.
     * Called by ScreenManager after a player logs in to restore their saved audio preferences.
     *
     * @param settings the Settings from the current player's PlayerProfile; no-op if null
     */
    public void applySettings(Settings settings) {
        if (settings == null) return;
        setVolume(settings.getVolume());
        setMusicEnabled(settings.isMusicEnabled());
        setSFXEnabled(settings.isSoundEffectsEnabled());
    }
}
