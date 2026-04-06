package main.modes;

import main.engine.GameSession;
import main.gameplay.Word;

/**
 * Interface for all game modes in KeyHunter.
 *
 * GameEngine uses this to handle mode-specific behaviour like spawn rate,
 * difficulty, lives, and when the game ends.
 *
 * Implementations include {@link NormalMode}, {@link EndlessMode}, and {@link TimedMode}.
 * @author Imad Tahir
 */
public interface GameMode {

     /**
     * Returns the type of this mode.
     *
     * @return the {@link ModeType}
     */
    public ModeType getType();

    /**
     * Returns how many lives the player starts with.
     *
     * @return starting lives
     */
    public int getInitialLives();

    /**
     * Chooses a difficulty based on the current level or progress.
     *
     * @param level current level (may be ignored in some modes)
     * @return a {@link Difficulty}
     */
    public Difficulty getDifficultyForLevel(int level); // This one should be overrided with int level in signature

    /**
     * Called when the player types a word correctly before the TTL expires.
     *
     * @param session current game session
     * @param word word that was typed correctly
     */
    public void onWordCorrect(GameSession session, Word word);

    /**
     * Called when a word is missed (TTL expires before completion).
     *
     * @param session current game session
     * @param word the word that was missed
     */
    public void onWordIncorrect(GameSession session, Word word);

    /**
     * Checks if the game should end.
     *
     * @param session current game session
     * @return true if game is finished
     */
    public boolean isSessionComplete(GameSession session);

    /**
     * Returns the interval, in seconds, between consecutive word spawns for the given level.
     *
     * @param level the current level
     * @return spawn interval in seconds
     */
    public double getSpawnRate(int level);

    /**
     * Returns how long a word stays on screen.
     *
     * @param word the word
     * @param level the current level
     * @return time to live (seconds)
     */
    public double getTargetTTL(Word word, int level);
}