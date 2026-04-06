package main.account;

/**
 * Stores a player's audio and gameplay preferences in KeyHunter.
 * Each PlayerProfile owns one Settings instance.
 * Defaults are: volume = 5, music on, sound effects on.
 *
 * @author Jaideep Singh
 * @see PlayerProfile
 */
public class Settings {

    // Volume level from 0 (mute) to 10 (max)
    private int volume;

    // Whether background music is turned on
    private boolean musicEnabled;

    // Whether sound effects are turned on
    private boolean soundEffectsEnabled;

    /**
     * Creates a Settings object with default values.
     * Volume starts at 5, music and sound effects are both enabled.
     */
    public Settings() {
        this.volume = 5;
        this.musicEnabled = true;
        this.soundEffectsEnabled = true;
    }

    /**
     * Creates a Settings object with specific values.
     * Used when loading a player's saved settings from disk.
     * Volume is validated and ignored if outside the 0-10 range.
     *
     * @param volume              the volume level (0-10)
     * @param musicEnabled        true to enable background music
     * @param soundEffectsEnabled true to enable sound effects
     */
    public Settings(int volume, boolean musicEnabled, boolean soundEffectsEnabled) {
        this.musicEnabled = musicEnabled;
        this.soundEffectsEnabled = soundEffectsEnabled;
        setVolume(volume);
    }

    /**
     * Returns the current volume level.
     *
     * @return the volume (0-10)
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Sets the volume level. Values outside 0-10 are ignored.
     *
     * @param volume the desired volume (0-10)
     */
    public void setVolume(int volume) {
        if (volume >= 0 && volume <= 10) {
            this.volume = volume;
        }
    }

    /**
     * Returns whether background music is enabled.
     *
     * @return true if music is on, false otherwise
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Turns background music on or off.
     *
     * @param musicEnabled true to enable music, false to disable it
     */
    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    /**
     * Returns whether sound effects are enabled.
     *
     * @return true if sound effects are on, false otherwise
     */
    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }

    /**
     * Turns sound effects on or off.
     *
     * @param soundEffectsEnabled true to enable sound effects, false to disable them
     */
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        this.soundEffectsEnabled = soundEffectsEnabled;
    }
}
