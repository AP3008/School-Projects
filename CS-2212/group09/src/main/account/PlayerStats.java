package main.account;

import main.modes.ModeType;

/**
 * Keeps track of everything a player has accomplished across all their sessions.
 *
 * <p>Every {@link PlayerProfile} owns exactly one {@code PlayerStats} instance.
 * It records things like average WPM, accuracy, total time played, and the
 * best scores ever achieved in each game mode.</p>
 *
 * @author Jaideep Singh
 * @see PlayerProfile
 */
public class PlayerStats {
    private double averageWPM;
    private double peakWPM;
    private double overallAccuracy;
    private int totalWordsCorrect;
    private int totalSessions;
    private int totalErrors;
    private long totalTimePlayed;
    private int highScore;
    private int highScoreNormal;
    private int highScoreTimed;
    private int highScoreEndless;
    private int highestLevel;

    /**
     * Creates a fresh {@code PlayerStats} with everything zeroed out.
     *
     * <p>{@code highestLevel} starts at 1 rather than 0 because every player
     * can access at least level 1. Called automatically when a new
     * {@link PlayerProfile} is created, and again whenever {@link #resetStats()}
     * wipes the slate clean.</p>
     */
    public PlayerStats() {
        averageWPM = 0.0; peakWPM = 0.0;
        overallAccuracy = 0.0; totalWordsCorrect = 0; totalSessions = 0;
        totalErrors = 0; totalTimePlayed = 0; highScore = 0;
        highScoreNormal = 0; highScoreTimed = 0; highScoreEndless = 0;
        highestLevel = 1;
    }

    /**
     * Updates the rolling stats after a game session finishes.
     *
     * <p>Recalculates {@code averageWPM} and {@code overallAccuracy} as running
     * averages weighted by sessions played so far. Updates {@code peakWPM} if
     * the session WPM is a new personal best. Called by
     * {@code ScreenManager.showGameOver()} alongside
     * {@link #updateExtendedStats(int, long, int, int, ModeType)}.</p>
     *
     * @param newWPM       the words-per-minute rate the player hit this session
     * @param accuracy     the typing accuracy (0.0-100.0) for this session
     * @param wordsCorrect the number of words typed correctly this session
     */
    public void updateStats(double newWPM, double accuracy, int wordsCorrect) {
        if (newWPM > peakWPM) peakWPM = newWPM;
        averageWPM = ((averageWPM * totalSessions) + newWPM) / (totalSessions + 1);
        overallAccuracy = ((overallAccuracy * totalSessions) + accuracy) / (totalSessions + 1);
        totalWordsCorrect += wordsCorrect;
        totalSessions++;
    }

    /**
     * Internal helper that handles bookkeeping common to all game modes:
     * errors, time played, overall high score, and highest level reached.
     *
     * <p>The public overload calls this first, then handles the per-mode
     * high score on top.</p>
     *
     * @param errors           typing errors made during the session
     * @param timePlayedMillis how long the session lasted, in milliseconds
     * @param score            the final score earned this session
     * @param levelReached     the highest level reached; capped at 10
     */
    private void updateExtendedStats(int errors, long timePlayedMillis, int score, int levelReached) {
        totalErrors += errors;
        totalTimePlayed += timePlayedMillis;
        if (score > highScore) highScore = score;
        if (levelReached > highestLevel) highestLevel = Math.min(levelReached, 10);
    }

    /**
     * Updates all extended stats after a session and routes the score to the
     * right per-mode high score field.
     *
     * <p>Delegates shared bookkeeping to the private overload, then checks
     * whether the score beats the best for the specific mode. Called by
     * {@code ScreenManager.showGameOver()} alongside
     * {@link #updateStats(double, double, int)}.</p>
     *
     * @param errors           typing errors made during the session
     * @param timePlayedMillis how long the session lasted, in milliseconds
     * @param score            the final score earned this session
     * @param levelReached     the highest level reached; capped at 10
     * @param modeType         the game mode played -- determines which per-mode
     *                         high score field gets updated
     */
    public void updateExtendedStats(int errors, long timePlayedMillis, int score, int levelReached, ModeType modeType) {
        updateExtendedStats(errors, timePlayedMillis, score, levelReached);
        if (modeType == ModeType.NORMAL && score > highScoreNormal) highScoreNormal = score;
        else if (modeType == ModeType.TIMED && score > highScoreTimed) highScoreTimed = score;
        else if (modeType == ModeType.ENDLESS && score > highScoreEndless) highScoreEndless = score;
    }

    /**
     * Wipes all stats back to their starting values, as if the account were brand new.
     *
     * <p>Everything goes to zero except {@code highestLevel}, which resets to 1.
     * Called by {@code ParentalControlService#resetPlayerStats(String)} after the
     * parent enters their PIN on {@code ResetStatsScreen}.</p>
     */
    public void resetStats() {
        averageWPM = 0.0; peakWPM = 0.0;
        overallAccuracy = 0.0; totalWordsCorrect = 0; totalSessions = 0;
        totalErrors = 0; totalTimePlayed = 0; highScore = 0;
        highScoreNormal = 0; highScoreTimed = 0; highScoreEndless = 0;
        highestLevel = 1;
    }

    /** @return the player's average WPM across all sessions */
    public double getAverageWPM() { return averageWPM; }

    /** @return the highest WPM the player has ever hit in a single session */
    public double getPeakWPM() { return peakWPM; }

    /** @return the player's overall typing accuracy across all sessions */
    public double getOverallAccuracy() { return overallAccuracy; }

    /** @return the total number of words typed correctly across all sessions */
    public int getTotalWordsCorrect() { return totalWordsCorrect; }

    /** @param v the average WPM to set (used when loading from saved data) */
    public void setAverageWPM(double v) { averageWPM = v; }

    /** @param v the peak WPM to set (used when loading from saved data) */
    public void setPeakWPM(double v) { peakWPM = v; }

    /** @param v the overall accuracy to set (used when loading from saved data) */
    public void setOverallAccuracy(double v) { overallAccuracy = v; }

    /** @param v the total words correct to set (used when loading from saved data) */
    public void setTotalWordsCorrect(int v) { totalWordsCorrect = v; }

    /** @return how many game sessions the player has completed */
    public int getTotalSessions() { return totalSessions; }

    /** @param v the session count to set (used when loading from saved data) */
    public void setTotalSessions(int v) { totalSessions = v; }

    /** @return the total number of typing errors made across all sessions */
    public int getTotalErrors() { return totalErrors; }

    /** @param v the error count to set (used when loading from saved data) */
    public void setTotalErrors(int v) { totalErrors = v; }

    /** @return the total time the player has spent in-game, in milliseconds */
    public long getTotalTimePlayed() { return totalTimePlayed; }

    /** @param v the total time played to set, in milliseconds (used when loading from saved data) */
    public void setTotalTimePlayed(long v) { totalTimePlayed = v; }

    /** @return the player's all-time high score across all modes */
    public int getHighScore() { return highScore; }

    /** @param v the overall high score to set (used when loading from saved data) */
    public void setHighScore(int v) { highScore = v; }

    /** @return the player's best score in Normal mode */
    public int getHighScoreNormal() { return highScoreNormal; }

    /** @param v the Normal mode high score to set (used when loading from saved data) */
    public void setHighScoreNormal(int v) { highScoreNormal = v; }

    /** @return the player's best score in Timed mode */
    public int getHighScoreTimed() { return highScoreTimed; }

    /** @param v the Timed mode high score to set (used when loading from saved data) */
    public void setHighScoreTimed(int v) { highScoreTimed = v; }

    /** @return the player's best score in Endless mode */
    public int getHighScoreEndless() { return highScoreEndless; }

    /** @param v the Endless mode high score to set (used when loading from saved data) */
    public void setHighScoreEndless(int v) { highScoreEndless = v; }

    /**
     * Returns the best score ever recorded for a specific game mode.
     *
     * <p>Falls back to the overall high score if an unrecognised
     * {@code ModeType} is passed in. Used by high-score screens and
     * {@code ParentalControlService} when showing per-mode records.</p>
     *
     * @param modeType the game mode to look up
     * @return the best score for that mode, or the overall high score as a fallback
     */
    public int getHighScoreForMode(ModeType modeType) {
        switch (modeType) {
            case NORMAL: return highScoreNormal;
            case TIMED: return highScoreTimed;
            case ENDLESS: return highScoreEndless;
            default: return highScore;
        }
    }

    /** @return the highest level the player has ever reached (capped at 10) */
    public int getHighestLevel() { return highestLevel; }

    /** @param v the highest level to set; automatically capped at 10 */
    public void setHighestLevel(int v) { highestLevel = Math.min(v, 10); }
}
