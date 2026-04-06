package main.engine;

import main.account.PlayerProfile;
import main.modes.ModeType;

/**
 * Holds all mutable state for a single game session.
 *
 * Created and owned by {@link GameEngine#startNewSession(PlayerProfile, ModeType, int)}.
 * Tracks the current player, mode, level, score, lives, elapsed time, and {@link GameState}.
 * At session end, {@link #createRunResult(double, AccuracyTracker)} packages the data into
 * a {@link RunResult} for GameOverScreen, CongratulationsScreen, LevelCompleteScreen,
 * and PlayerStats.
 *
 * @author Adam Porbanderwalla
 */
public class GameSession {

    private PlayerProfile player;
    private ModeType modeType;
    private int level = 1;
    private int score = 0;
    private int lives = 3;
    private GameState state;
    private long elapsedTime = 0;

    /**
     * Constructs a new GameSession in the {@link GameState#RUNNING} state.
     *
     * Called exclusively by {@link GameEngine#startNewSession(PlayerProfile, ModeType, int)}.
     * Lives are subsequently overridden by {@link GameEngine} via {@link #setLives(int)}.
     *
     * @param player        the player whose profile will be updated on session end
     * @param modeType      the game mode (NORMAL, ENDLESS, or TIMED)
     * @param startingLevel the level at which the session begins
     */
    public GameSession(PlayerProfile player, ModeType modeType, int startingLevel){
        this.player = player;
        this.modeType = modeType;
        level = startingLevel;
        this.state = GameState.RUNNING;
    }

    /**
     * Adds points to the current score, clamping to zero if the result would be negative.
     *
     * Called by {@link ScoreManager#applyCorrectWord(GameSession, main.gameplay.Word)} on word
     * completion and by {@link ScoreManager#applyMistake(GameSession)} when a word escapes.
     *
     * @param points the points to add; may be negative for penalties
     */
    public void addScore(int points){
        this.score += points;
        if (this.score < 0) this.score = 0;
    }

    /**
     * Decrements the remaining life count by one, down to a minimum of zero.
     *
     * Called by {@link main.modes.GameMode} implementations when a word escapes.
     */
    public void loseLife(){
        if (this.lives > 0) this.lives  = 1;
    }

    /**
     * Increments the remaining life count by one.
     *
     * Called by {@link GameEngine#handleKeystroke(char)} when a completed
     * {@link main.gameplay.WordTarget} carries an extra life power up.
     */
    public void gainLife(){
        this.lives += 1;
    }

    /**
     * Sets the current {@link GameState} of this session.
     *
     * Used by {@link GameEngine} to transition between RUNNING, PAUSED, and GAME_OVER.
     *
     * @param state the new game state
     */
    public void setState(GameState state){
        this.state = state;
    }

    /**
     * {@link GameState} getter method.
     *
     * Checked by {@link GameEngine#handleKeystroke(char)} and {@link GameEngine#update(double)}
     * before processing any input or logic.
     *
     * @return the current state
     */
    public GameState getState(){
        return this.state;
    }

    /**
     * Increments the current level by one.
     *
     * Called by {@link main.modes.GameMode} implementations (e.g., NormalMode) when
     * level advance conditions are met.
     */
    public void advanceLevel(){
        this.level += 1;
    }

    /**
     * Resets the session to its initial in progress state.
     *
     * Resets score to 0, lives to 3, state to {@link GameState#RUNNING}, and elapsed time to 0.
     */
    public void reset(){
        this.score = 0;
        this.lives = 3;
        this.state = GameState.RUNNING;
        this.elapsedTime = 0;
    }

    /**
     * {@link main.account.PlayerProfile} getter method.
     *
     * Used by {@link GameEngine#endSession()} to update the player's lifetime stats and persist a high score entry.
     *
     * @return the player's profile
     */
    public PlayerProfile getPlayer(){
        return this.player;
    }

    /**
     * score getter. Displayed on the HUD in GameScreen and included in the {@link RunResult}.
     *
     * @return the current score (always >= 0)
     */
    public int getScore(){
        return this.score;
    }

    /**
     * Sets the number of remaining lives.
     *
     * Called by {@link GameEngine#startNewSession(PlayerProfile, ModeType, int)} to apply
     * the mode's initial life count.
     *
     * @param lives the number of lives to set
     */
    public void setLives(int lives){
        this.lives = lives;
    }

    /**
     * lives getter. Checked by {@link GameEngine} when deciding whether to grant an extra life power up.
     *
     * @return remaining lives
     */
    public int getLives(){
        return this.lives;
    }

    /**
     * level getter. Used by {@link GameEngine#update(double)} to determine spawn rate, word difficulty, and TTL.
     *
     * @return the current level (1 based)
     */
    public int getLevel(){
        return this.level;
    }

    /**
     * elapsedTime getter. Updated every tick by {@link GameEngine#update(double)}.
     *
     * @return elapsed time in milliseconds
     */
    public long getElapsedTime(){
        return this.elapsedTime;
    }

    /**
     * elapsedTime setter. Called by {@link GameEngine#update(double)} on every game tick.
     *
     * @param newElapsedTime the new elapsed time in milliseconds
     */
    public void setElapsedTime(long newElapsedTime){
        this.elapsedTime = newElapsedTime;
    }

    /**
     * modeType getter. Read by {@link GameEngine#update(double)} for mode specific logic and included in the {@link RunResult}.
     *
     * @return the mode type of this session
     */
    public ModeType getModeType(){
        return this.modeType;
    }

    /**
     * Creates and returns an immutable {@link RunResult} snapshot of this session.
     *
     * Called by {@link GameEngine#endSession()} after marking the session as {@link GameState#GAME_OVER}.
     * The level is capped at 10 before being stored.
     *
     * @param wpm      words per minute, pre calculated by {@link GameEngine#endSession()}
     * @param accuracy the {@link AccuracyTracker} owned by {@link GameEngine}
     * @return an immutable {@link RunResult} for this session
     */
    public RunResult createRunResult(double wpm, AccuracyTracker accuracy){
        int timeSeconds = (int)(elapsedTime / 1000);
        int cappedLevel = Math.min(level, 10);
        RunResult rr = new RunResult(this.modeType, this.score, wpm, accuracy.getAccuracyPercent(), accuracy.getErrorCount(), timeSeconds, cappedLevel);
        return rr;
    }
}
