package main.account;

import main.persistence.HighScoreTable;

/**
 * Handles parental control actions in KeyHunter.
 * A parent must enter the correct PIN before any admin action is allowed.
 * Supports viewing stats, resetting stats, resetting passwords, and clearing high scores.
 *
 * @author Jaideep Singh
 * @see AccountManager
 * @see PlayerStats
 */
public class ParentalControlService {

    // The PIN needed to unlock parental controls
    private String pin;

    // Used to look up and modify player accounts
    private AccountManager accountManager;

    // The global high score table that can be cleared
    private HighScoreTable highScoreTable;

    /**
     * Creates a ParentalControlService with a PIN, account manager, and high score table.
     *
     * @param pin            the PIN required to access parental controls
     * @param accountManager manages player accounts and saving data
     * @param highScoreTable the global high score table; can be null
     */
    public ParentalControlService(String pin, AccountManager accountManager, HighScoreTable highScoreTable) {
        this.pin = pin;
        this.accountManager = accountManager;
        this.highScoreTable = highScoreTable;
    }

    /**
     * Checks if the given PIN matches the stored parental PIN.
     *
     * @param inputPin the PIN entered by the parent
     * @return true if the PIN is correct, false otherwise
     */
    public boolean verifyPin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    /**
     * Returns the stats for a given player.
     * Returns null if no account with that username exists.
     *
     * @param username the player's username
     * @return the player's PlayerStats, or null if not found
     */
    public PlayerStats viewPlayerStats(String username) {
        PlayerProfile profile = accountManager.findPlayer(username);
        if (profile != null) {
            return profile.getStats();
        }
        return null;
    }

    /**
     * Resets the password for a given player account.
     * Does nothing if the username doesn't exist.
     *
     * @param username    the account to update
     * @param newPassword the new password to set
     */
    public void resetPassword(String username, String newPassword) {
        accountManager.resetPassword(username, newPassword);
    }

    /**
     * Resets all stats for a given player back to zero and saves the change.
     * Does nothing if the username doesn't exist.
     *
     * @param username the player whose stats should be reset
     */
    public void resetPlayerStats(String username) {
        PlayerProfile profile = accountManager.findPlayer(username);
        if (profile != null) {
            profile.getStats().resetStats();
            accountManager.saveAll();
        }
    }

    /**
     * Clears the global high score table.
     * Does nothing if the high score table is null.
     */
    public void resetHighScores() {
        if (highScoreTable != null) {
            highScoreTable.reset();
        }
    }

}
