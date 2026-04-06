package main.engine;

/**
 * Tracks keystroke and word-completion accuracy for a single game session. 
 *
 * An Accuracy instance is used for a {@link GameEngine}, and is reset
 * at the start of every new session via {@link #reset()}. {@link GameEngine#endSession()} reads
 * {@link #getAccuracyPercent()}, {@link #getErrorCount()}, and {@link #getWordsCorrect()}
 * to build the {@link RunResult} and update the player's {@link main.account.PlayerProfile}.
 *
 * @author Adam Porbanderwalla
 * @see GameEngine
 * @see RunResult
 * @see main.account.PlayerProfile
 */
public class AccuracyTracker {

    private int typedChars;
    private int correctChars;
    private int errorCount;
    private int wordsCorrect;

    /**
     * Constructs a new AccuracyTracker with all counters initialised to zero.
     *
     * Called once by {@link GameEngine} during construction. Use {@link #reset()}
     * to clear counters between sessions rather than creating a new instance.</p>
     */
    public AccuracyTracker(){
        this.typedChars = 0;
        this.correctChars = 0;
        this.errorCount = 0;
        this.wordsCorrect = 0;
    }

    /**
     * Resets all accuracy counters to zero.
     *
     * Called by {@link GameEngine#startNewSession(main.account.PlayerProfile, main.modes.ModeType, int)}
     * at the beginning of each new game session so that statistics from previous
     * sessions do not mess with current one.
     */
    public void reset(){
        this.typedChars = 0;
        this.correctChars = 0;
        this.errorCount = 0;
        this.wordsCorrect = 0;
    }

    /**
     * Increments typed chars and identifies if it is correct or not.
     *
     * Called by {@link GameEngine#handleKeystroke(char)} for every key press.
     * When the actual character matches the player's input we increment correctChars,
     * otherwise we increment errorCount.
     * @param expected the character the player was supposed to type
     * @param actual   the character the player actually typed
     */
    public void recordKeystroke(char expected, char actual){
        typedChars += 1;
        if (expected == actual){
            correctChars += 1;
        } else {
            errorCount += 1;
        }
    }

    /**
     * Returns the keystroke accuracy as a percentage.
     *
     * {@link GameSession#createRunResult(double, AccuracyTracker)} and
     * {@code GameScreen} to display live accuracy on the HUD.
     *
     * @return accuracy percentage 
     */
    public double getAccuracyPercent(){
        if (typedChars == 0) return 0.0;
        return 100.0 * correctChars / typedChars;
    }

    /**
     * {@link AccuracyTracker#errorCount} getter method
     *
     * Read by {@link GameSession#createRunResult(double, AccuracyTracker)} to
     * populate {@link RunResult#getErrors()} and by {@link GameEngine#endSession()}
     * to update PlayerStats.
     *
     * @return the error count 
     */
    public int getErrorCount(){
        return errorCount;
    }

    /**
     * Increments the count of fully completed words by one.
     *
     * Called by {@link GameEngine#handleKeystroke(char)} after a
     * {@link main.gameplay.WordTarget} is marked as completed. The running total is
     * used by {@link GameEngine#endSession()} to calculate the WPM.
     */
    public void recordWordCorrect(){
        wordsCorrect += 1;
    }

    /**
     * {@link AccuracyTracker#wordsCorrect} getter method
     *
     * Used by {@link GameEngine#endSession()} to calculate WPM.  
     * @return the count of correctly completed words (always &ge; 0)
     */
    public int getWordsCorrect(){
        return wordsCorrect;
    }
}