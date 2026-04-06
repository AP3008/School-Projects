package main.engine;

import main.gameplay.Word;

/**
 * Manages score calculation, consecutive word streaks, and timed power ups for a game session.
 *
 * Owned by {@link GameEngine} and reset at the start of every session via {@link #reset()}.
 * On each successful word completion, {@link GameEngine#handleKeystroke(char)} calls
 * {@link #applyCorrectWord(GameSession, Word)} and {@link #recordCorrectWord()}.
 * When a word escapes, {@link GameEngine#update(double)} calls {@link #applyMistake(GameSession)}
 * and {@link #resetStreak()}. Power up timers are ticked by {@link #update(double)}.
 *
 * Two timed power ups are supported: double points (activated after a streak of
 * {@link #STREAK_THRESHOLD} correct words, lasts {@link #POWER_UP_DURATION} seconds) and
 * slowdown (activated when a slowdown tagged target is completed, lasts {@link #SLOWDOWN_DURATION} seconds).
 *
 * @author
 */
public class ScoreManager {

    private int pointsPerChar;
    private int mistakePenalty;
    private boolean doublePointsActive;
    private double doublePointsRemaining;
    private boolean slowdownActive;
    private double slowdownRemaining;
    private int currentStreak;
    
    /**
     * Number of consecutive correct words required to activate the double points power up.
     * Currently set to 10.
     */
    private static final int STREAK_THRESHOLD = 10;

    /**
     * Duration in seconds of the double points power up once activated.
     * Currently set to 10.0 seconds.
     */
    private static final double POWER_UP_DURATION = 10.0;

    /**
     * Duration in seconds of the slowdown power up once activated.
     * Currently set to 5.0 seconds.
     */
    private static final double SLOWDOWN_DURATION = 5.0;

    /**
     * Constructs a new ScoreManager.
     *
     * Called once by {@link GameEngine} with pointsPerChar=10, mistakePenalty= 5, doublePointsActive=false.
     *
     * @param pointsPerChar      base points per character of a correctly typed word
     * @param mistakePenalty     points delta when a word escapes (typically negative)
     * @param doublePointsActive initial state of the double points power up
     */
    public ScoreManager(int pointsPerChar, int mistakePenalty, boolean doublePointsActive){
        this.pointsPerChar = pointsPerChar;
        this.mistakePenalty = mistakePenalty;
        this.doublePointsActive = doublePointsActive;
        this.doublePointsRemaining = 0.0;
        this.slowdownActive = false;
        this.slowdownRemaining = 0.0;
        this.currentStreak = 0;
    }

    /**
     * Awards points to the session for a correctly completed word.
     *
     * Points are calculated as pointsPerChar * word.length(), doubled when double points is active.
     * Delegates to {@link GameSession#addScore(int)}. Called by {@link GameEngine#handleKeystroke(char)}.
     *
     * @param session the current game session whose score is updated
     * @param word    the word that was correctly completed
     */
    public void applyCorrectWord(GameSession session, Word word){
        int points = this.pointsPerChar * word.getText().length();
        if (doublePointsActive) points *= 2;
        session.addScore(points);
    }

    /**
     * Applies the mistake penalty to the session score when a word escapes the screen.
     *
     * Delegates to {@link GameSession#addScore(int)} with the negative mistakePenalty value.
     * Called by {@link GameEngine#update(double)} for each escaped target.
     *
     * @param session the current game session whose score is penalised
     */
    public void applyMistake(GameSession session){
        session.addScore(mistakePenalty);
    }

    /**
     * Increments the streak and activates double points when the streak reaches {@link #STREAK_THRESHOLD}.
     *
     * Called by {@link GameEngine#handleKeystroke(char)} after a target is completed.
     * Streak resets to zero after triggering the power up.
     */
    public void recordCorrectWord() {
        currentStreak++;
        if (currentStreak >= STREAK_THRESHOLD && !doublePointsActive) {
            activateDoublePoints(POWER_UP_DURATION);
            currentStreak = 0;
        }
    }

    /**
     * Resets the streak to zero.
     *
     * Called by {@link GameEngine#update(double)} each time a target escapes the screen.
     */
    public void resetStreak() {
        currentStreak = 0;
    }

    /**
     * Activates the double points power up for the specified duration.
     *
     * Private helper called by {@link #recordCorrectWord()} when the streak threshold is reached.
     *
     * @param durationSeconds how long (in seconds) the double points effect lasts
     */
    private void activateDoublePoints(double durationSeconds) {
        doublePointsActive = true;
        doublePointsRemaining = durationSeconds;
    }

    /**
     * Activates the slowdown power up for {@link #SLOWDOWN_DURATION} seconds.
     *
     * Called by {@link GameEngine#handleKeystroke(char)} when a completed target carries the slowdown flag.
     * While active, {@link GameEngine#update(double)} halves target movement speed and (in Timed mode) the clock rate.
     */
    public void activateSlowdown() {
        slowdownActive = true;
        slowdownRemaining = SLOWDOWN_DURATION;
    }

    /**
     * Advances the power up countdown timers by one frame.
     *
     * Called by {@link GameEngine#update(double)} once per tick. Decrements doublePointsRemaining
     * and slowdownRemaining by deltaSeconds, deactivating each when its timer reaches zero.
     *
     * @param deltaSeconds seconds elapsed since the last tick
     */
    public void update(double deltaSeconds) {
        if (doublePointsActive) {
            doublePointsRemaining -= deltaSeconds;
            if (doublePointsRemaining <= 0) {
                doublePointsActive = false;
                doublePointsRemaining = 0.0;
            }
        }
        if (slowdownActive) {
            slowdownRemaining -= deltaSeconds;
            if (slowdownRemaining <= 0) {
                slowdownActive = false;
                slowdownRemaining = 0.0;
            }
        }
    }

    /**
     * Resets all power up states and the streak counter to their initial values.
     *
     * Called by {@link GameEngine#startNewSession(main.account.PlayerProfile, main.modes.ModeType, int)}
     * at the start of each new session.
     */
    public void reset() {
        doublePointsActive = false;
        doublePointsRemaining = 0.0;
        slowdownActive = false;
        slowdownRemaining = 0.0;
        currentStreak = 0;
    }

    /**
     * doublePointsActive getter. Read by GameScreen via {@link GameEngine#getScoreManager()} to render the HUD indicator.
     *
     * @return true if double points are currently being applied
     */
    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }

    /**
     * slowdownActive getter. Read by {@link GameEngine#update(double)} and GameScreen via {@link GameEngine#getScoreManager()}.
     *
     * @return true if the slowdown effect is currently applied
     */
    public boolean isSlowdownActive() {
        return slowdownActive;
    }

    /**
     * currentStreak getter. Exposed via {@link GameEngine#getScoreManager()} so GameScreen can display the live streak on the HUD.
     *
     * @return the current streak count
     */
    public int getStreak() {
        return currentStreak;
    }
}
