package main.account;

import main.persistence.PersistenceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all player accounts in KeyHunter.
 * Handles login, account creation, password resets, and saving data to disk.
 *
 * @author Jaideep Singh
 * @see PlayerProfile
 * @see PlayerStats
 */
public class AccountManager {

    // Stores all player accounts, looked up by username
    private Map<String, PlayerProfile> accounts;

    // The player currently logged in, or null if no one is
    private PlayerProfile currentPlayer;

    // Used to load and save player data from a JSON file
    private PersistenceService persistence;

    /**
     * Sets up the AccountManager and loads all saved accounts from disk.
     *
     * @param persistence used to read and write player data to disk
     */
    public AccountManager(PersistenceService persistence) {
        this.persistence = persistence;
        this.accounts = new HashMap<>();
        this.currentPlayer = null;
        loadAll();
    }

    /**
     * Loads all saved player profiles from disk into the accounts map.
     * Called automatically when the AccountManager is created.
     */
    private void loadAll() {
        List<PlayerProfile> players = persistence.loadPlayers();
        accounts.clear();
        for (PlayerProfile p : players) {
            accounts.put(p.getUsername(), p);
        }
    }

    /**
     * Saves all player profiles to disk.
     * Called after any change to account data so nothing is lost.
     */
    public void saveAll() {
        persistence.savePlayers(new ArrayList<>(accounts.values()));
    }

    /**
     * Logs in a player if the username and password are correct.
     *
     * @param username the player's username
     * @param password the player's password
     * @return true if login was successful, false otherwise
     */
    public boolean login(String username, String password) {
        PlayerProfile p = accounts.get(username);
        if (p != null && p.getPassword().equals(password)) {
            currentPlayer = p;
            return true;
        }
        return false;
    }

    /**
     * Creates a new player account and saves it to disk.
     * Fails if the username is already taken or either field is blank.
     *
     * @param username the username for the new account
     * @param password the password for the new account
     * @return true if the account was created, false otherwise
     */
    public boolean createAccount(String username, String password) {
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;
        if (accounts.containsKey(username)) return false;
        accounts.put(username, new PlayerProfile(username, password));
        saveAll();
        return true;
    }

    /**
     * Finds and returns a player profile by username.
     * Returns null if no account with that username exists.
     *
     * @param username the username to search for
     * @return the matching PlayerProfile, or null if not found
     */
    public PlayerProfile findPlayer(String username) {
        return accounts.get(username);
    }

    /**
     * Changes the password for the given account and saves the change.
     * Does nothing if the username doesn't exist.
     *
     * @param username    the account to update
     * @param newPassword the new password to set
     */
    public void resetPassword(String username, String newPassword) {
        PlayerProfile p = accounts.get(username);
        if (p != null) {
            p.setPassword(newPassword);
            saveAll();
        }
    }

    /**
     * Logs out the current player and saves all account data.
     * After this, getCurrentPlayer() will return null.
     */
    public void logout() {
        saveAll();
        this.currentPlayer = null;
    }

    /**
     * Returns the player who is currently logged in.
     * Returns null if no one is logged in.
     *
     * @return the current PlayerProfile, or null if no active session
     */
    public PlayerProfile getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the full map of all registered accounts.
     * The key is the username and the value is the PlayerProfile.
     *
     * @return a map of all accounts
     */
    public Map<String, PlayerProfile> getAllAccounts() {
        return accounts;
    }
}
