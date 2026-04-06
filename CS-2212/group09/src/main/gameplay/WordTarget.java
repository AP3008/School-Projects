package main.gameplay;

/**
 * Represents an active word target on the game screen that the player must type.
 * <p>
 * Wraps a {@link Word} and tracks the player's typing progress, how long the
 * target has been on screen, and whether it carries any power-ups. If the player
 * does not complete the word before the TTL expires, the target escapes and the
 * player loses a life.
 *
 * @author Garv Sharma
 * @see Word
 * @see TargetManager
 */
public class WordTarget{

      /** The word the player must type. */
    private Word word;

    /** Index of the next character the player needs to type (0-based). */
    private int progressIndex;

    /** Whether this target has escaped without being completed. */
    private boolean escaped;

    /** Total time in seconds this target has been on screen. */
    private double timeAlive;

    /** Time in seconds before this target escapes if not completed. */
    private double ttl;

    /** Whether completing this word activates a slowdown power-up. */
    private boolean hasSlowdownPowerup;

    /** Whether completing this word grants the player an extra life. */
    private boolean hasExtraLifePowerup;

     /**
     * Constructs a new WordTarget for the given word.
     *
     * @param word  the word this target represents
     * @param speed the movement speed used by the GUI sprite (not used directly here)
     * @param ttl   time in seconds before this target escapes if not completed
     */
    public WordTarget(Word word, double speed, double ttl) {
        this.word = word;
        this.ttl = ttl;
        this.progressIndex = 0;
        this.escaped = false;
        this.timeAlive = 0.0;
        this.hasSlowdownPowerup = false;
        this.hasExtraLifePowerup = false;
    }

     /**
     * Returns the word associated with this target.
     *
     * @return the word the player must type
     */
    public Word getWord() {
        return word;
    }

    /**
     * Returns the current typing progress index.
     * Represents how many characters have been correctly typed so far.
     *
     * @return the number of characters correctly typed
     */
    public int getProgressIndex() {
        return progressIndex;
    }

    /**
     * Returns whether this target has escaped.
     *
     * @return true if the target escaped before being completed
     */
    public boolean hasEscaped() {
        return escaped;
    }

        /**
     * Marks this target as escaped. Called internally when the TTL expires.
     */
    private void markEscaped() {
        escaped = true;
    }

    /**
     * Advances the typing progress by one character.
     * Has no effect if the word is already fully typed.
     */
    public void advanceProgress() {
        if (progressIndex < word.getText().length()) {
            progressIndex++;
        }
    }

    /**
     * Returns whether the player has finished typing the entire word.
     *
     * @return true if all characters have been typed correctly
     */
    public boolean isCompleted() {
        return progressIndex >= word.getText().length();
    }

    /**
     * Updates this target each game frame. Accumulates time and marks
     * the target as escaped if the TTL has been exceeded.
     *
     * @param deltaSeconds time elapsed since the last frame in seconds
     */
    public void update(double deltaSeconds) {
        timeAlive += deltaSeconds;
        if (timeAlive >= ttl && !escaped) {
            markEscaped();
        }
    }

    /**
     * Returns how long this target has been on screen.
     *
     * @return time alive in seconds
     */
    public double getTimeAlive() {
        return timeAlive;
    }

    /**
     * Returns the time-to-live for this target.
     *
     * @return TTL in seconds
     */
    public double getTtl() {
        return ttl;
    }

    /**
     * Returns whether completing this target activates the slowdown power-up.
     *
     * @return true if this target has a slowdown power-up
     */
    public boolean hasSlowdownPowerup() {
        return hasSlowdownPowerup;
    }

    /**
     * Sets whether this target carries a slowdown power-up.
     *
     * @param hasSlowdownPowerup true to enable the slowdown power-up
     */
    public void setSlowdownPowerup(boolean hasSlowdownPowerup) {
        this.hasSlowdownPowerup = hasSlowdownPowerup;
    }

    /**
     * Returns whether completing this target grants the player an extra life.
     *
     * @return true if this target has an extra life power-up
     */
    public boolean hasExtraLifePowerup() {
        return hasExtraLifePowerup;
    }

    /**
     * Sets whether this target carries an extra life power-up.
     *
     * @param hasExtraLifePowerup true to enable the extra life power-up
     */
    public void setExtraLifePowerup(boolean hasExtraLifePowerup) {
        this.hasExtraLifePowerup = hasExtraLifePowerup;
    }
}