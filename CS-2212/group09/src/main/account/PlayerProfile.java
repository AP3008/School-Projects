package main.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single player's account in KeyHunter.
 * Stores the player's username, password, stats, settings, and unlocked levels.
 * Level 1 is unlocked by default when the account is created.
 *
 * @author Jaideep Singh
 * @see AccountManager
 * @see PlayerStats
 * @see Settings
 */
public class PlayerProfile {

    // The player's unique login name
    private String username;

    // The player's password, stored as plain text
    private String password;

    // Tracks the player's long-term stats across all sessions
    private PlayerStats stats;

    // The list of levels (1-10) this player has unlocked
    private List<Integer> unlockedLevels;

    // The player's audio and gameplay preferences
    private Settings settings;

    /**
     * Creates a new PlayerProfile with the given username and password.
     * Starts with fresh stats, default settings, and level 1 unlocked.
     *
     * @param username the player's unique username
     * @param password the player's password
     */
    public PlayerProfile(String username, String password) {
        this.username = username;
        this.password = password;
        this.stats = new PlayerStats();
        this.settings = new Settings();
        this.unlockedLevels = new ArrayList<>();
        this.unlockedLevels.add(1);
    }

    /**
     * Returns the player's username.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the player's password.
     *
     * @return the player's current password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the player's password.
     *
     * @param newPassword the new password to set
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * Returns the player's stats. Creates a new one if it's null
     * (can happen after loading from JSON).
     *
     * @return the player's PlayerStats object
     */
    public PlayerStats getStats() {
        if (stats == null) {
            stats = new PlayerStats();
        }
        return stats;
    }

    /**
     * Returns the list of levels this player has unlocked.
     * The list cannot be modified directly — use unlockLevel() instead.
     *
     * @return an unmodifiable list of unlocked level numbers
     */
    public List<Integer> getUnlockedLevels() {
        return Collections.unmodifiableList(unlockedLevels);
    }

    /**
     * Sets the list of unlocked levels.
     * Used when loading a player's saved data from disk.
     *
     * @param unlockedLevels the list of unlocked level numbers to set
     */
    public void setUnlockedLevels(List<Integer> unlockedLevels) {
        this.unlockedLevels = unlockedLevels;
    }

    /**
     * Unlocks a level for this player if it hasn't been unlocked yet.
     * Only levels 1-10 are valid; anything outside that range is ignored.
     *
     * @param level the level number to unlock (1-10)
     */
    public void unlockLevel(int level) {
        if (level >= 1 && level <= 10 && !unlockedLevels.contains(level)) {
            unlockedLevels.add(level);
        }
    }

    /**
     * Checks if a specific level has been unlocked by this player.
     *
     * @param level the level number to check
     * @return true if the level is unlocked, false otherwise
     */
    public boolean isLevelUnlocked(int level) {
        return unlockedLevels.contains(level);
    }

    /**
     * Returns the player's settings. Creates a new one if it's null
     * (can happen after loading from JSON).
     *
     * @return the player's Settings object
     */
    public Settings getSettings() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    /**
     * Sets the player's settings.
     * Used when loading a player's saved preferences from disk.
     *
     * @param settings the Settings object to store
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
