package main.engine;

import main.modes.ModeType;

/**
 * Immutable snapshot of results for a completed game session.
 *
 * Produced by {@link GameSession#createRunResult(double, AccuracyTracker)} at the end of every
 * session and returned from {@link GameEngine#endSession()}. Consumed by GameOverScreen,
 * CongratulationsScreen, LevelCompleteScreen, and PlayerStats to display or persist performance metrics.
 *
 * @author Adam Porbanderwalla
 */
public class RunResult {

    private ModeType modeType;
    private int score;
    private double wpm;
    private double accuracyPercent;
    private int errors;
    private int durationSeconds;
    private int levelReached;

    /**
     * Constructs a RunResult. Called only by {@link GameSession#createRunResult(double, AccuracyTracker)}.
     *
     * @param modeType        the game mode that was played
     * @param score           the final score
     * @param wpm             words per minute achieved
     * @param accuracyPercent percentage of correct keystrokes (0–100)
     * @param errors          total number of incorrect keystrokes
     * @param durationSeconds total session duration in seconds
     * @param levelReached    highest level reached (capped at 10)
     */
    public RunResult(ModeType modeType, int score, double wpm, double accuracyPercent, int errors, int durationSeconds, int levelReached){
        this.modeType = modeType;
        this.score = score;
        this.wpm = wpm;
        this.accuracyPercent = accuracyPercent;
        this.errors = errors;
        this.durationSeconds = durationSeconds;
        this.levelReached = levelReached;
    }

    /**
     * score getter. Read by GameOverScreen, CongratulationsScreen, LevelCompleteScreen, and PlayerStats.
     *
     * @return the final score
     */
    public int getScore(){
        return score;
    }

    /**
     * wpm getter. Calculated in {@link GameEngine#endSession()} and passed via {@link GameSession#createRunResult(double, AccuracyTracker)}.
     *
     * @return words per minute
     */
    public double getWpm(){
        return wpm;
    }

    /**
     * accuracyPercent getter. Sourced from {@link AccuracyTracker#getAccuracyPercent()} at session end.
     *
     * @return accuracy as a value between 0.0 and 100.0
     */
    public double getAccuracyPercent(){
        return accuracyPercent;
    }

    /**
     * errors getter. Sourced from {@link AccuracyTracker#getErrorCount()} at session end.
     *
     * @return the total error count
     */
    public int getErrors(){
        return errors;
    }

    /**
     * durationSeconds getter. Derived from {@link GameSession#getElapsedTime()} divided by 1000.
     *
     * @return session duration in whole seconds
     */
    public int getDurationInSeconds(){
        return durationSeconds;
    }

    /**
     * levelReached getter. Level is capped at 10 inside {@link GameSession#createRunResult(double, AccuracyTracker)}.
     *
     * @return level reached (1–10)
     */
    public int getLevelReached(){
        return levelReached;
    }

    /**
     * modeType getter. Used by end of session screens and {@link main.account.PlayerStats}.
     *
     * @return the ModeType of this session
     */
    public ModeType getMode(){
        return modeType;
    }
}
