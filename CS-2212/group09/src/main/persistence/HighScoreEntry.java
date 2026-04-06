package main.persistence;

import main.modes.ModeType;

/**
 * Represents a single high score record for one player in one game mode.
 * Stores the player's username, score, game mode, and the time it was recorded.
 *
 * @author Rahul
 */
public class HighScoreEntry {

    // The username of the player who achieved this score
    private String username;

    // The score the player achieved
    private int score;

    // The game mode this score was achieved in
    private ModeType modeType;

    // The time this score was recorded, in milliseconds since the Unix epoch
    private long timestampMillis;

    /**
     * Creates a new HighScoreEntry with all required fields.
     *
     * @param username        the player's username
     * @param score           the score achieved
     * @param modeType        the game mode the score was achieved in
     * @param timestampMillis the time the score was recorded in milliseconds
     */
    public HighScoreEntry(String username, int score, ModeType modeType, long timestampMillis) {
        this.username = username;
        this.score = score;
        this.modeType = modeType;
        this.timestampMillis = timestampMillis;
    }

    /**
     * Returns the username of the player who achieved this score.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the score for this entry.
     *
     * @return the numeric score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the game mode this score was achieved in.
     *
     * @return the ModeType for this entry
     */
    public ModeType getModeType() {
        return modeType;
    }

    /**
     * Returns the time this score was recorded.
     *
     * @return the timestamp in milliseconds since the Unix epoch
     */
    public long getTimestampMillis() {
        return timestampMillis;
    }
}
