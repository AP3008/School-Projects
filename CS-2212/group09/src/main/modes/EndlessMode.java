package main.modes;

import main.engine.GameSession;
import main.gameplay.Word;
import java.util.Random;

/**
 * Endless mode where the player plays until they lose all lives.
 *
 * There are no levels. The game gets harder based on how many words the player has cleared.
 * The spawn rate and TTL of words scale with the total number of words cleared, 
 * creating a continuous difficulty ramp.
 * 
 * @author Imad Tahir
 * @see GameMode
 */
public class EndlessMode implements GameMode {

    /** Number of words the player has cleared so far */
    private int wordsCleared;

    /** Used to randomly pick difficulty */
    private final Random random = new Random();

    /** Constructs a new Endless Mode session, initialising the words-cleared counter to zero. */
    public EndlessMode() {
        this.wordsCleared = 0;
    }

    /**
     * Returns the type of this mode.
     *
     * @return {@link ModeType#ENDLESS}
     */
    public ModeType getType() {
        return ModeType.ENDLESS;
    }

   /**
     * Returns the starting number of lives for endless mode.
     *
     * @return 3 lives
     */
    public int getInitialLives() {
        return 3;
    }

    /**
     * Returns the spawn rate for words in endless mode.
     *
     * The level parameter is not used here. Instead, the spawn rate becomes faster as more words are cleared, 
     * but it will not go below 0.5 seconds.
     *
     * @param level current level (not used in endless mode)
     * @return spawn interval in seconds
     */
    public double getSpawnRate(int level) {
        double rate = 3.0 - (wordsCleared / 15.0) * 0.15;
        return Math.max(rate, 0.5);
    }

    /**
     * Chooses the difficulty of the next word.
     *
     * Endless mode uses a fixed random distribution:
     * EASY 35%, MEDIUM 45%, HARD 20%.
     *
     * @param level current level (not used in endless mode)
     * @return the selected {@link Difficulty}
     */
    public Difficulty getDifficultyForLevel(int level) {
        int roll = random.nextInt(100);
        if (roll < 35) {
            return Difficulty.EASY;
        } else if (roll < 80) {
            return Difficulty.MEDIUM;
        } else {
            return Difficulty.HARD;
        }
    }

    /**
     * Updates the mode when the player types a word correctly.
     *
     * In endless mode, this increases the number of cleared words,
     * which affects future spawn rate and target timing.
     *
     * @param session current game session
     * @param word word that was typed correctly
     */
    public void onWordCorrect(GameSession session, Word word) {
        wordsCleared++;
    }

    /**
     * Updates the mode when the player misses a word.
     *
     * In endless mode, missing a word causes the player to lose a life.
     *
     * @param session current game session
     * @param word word that was missed
     */
    public void onWordIncorrect(GameSession session, Word word) {
        session.loseLife();
    }

    /**
     * Returns how long a word stays on screen before disappearing.
     *
     * The base time depends on the word length. As more words are cleared,
     * the multiplier becomes smaller, which gives the player less time.
     *
     * @param word the word being displayed
     * @param level current level (not used in endless mode)
     * @return target TTL in seconds
     */
    public double getTargetTTL(Word word, int level) {
        double baseTTL = word.getText().length() * 0.8 + 2.0;
        double multiplier = Math.max(0.6, 1.3 - (wordsCleared / 30.0) * 0.1);
        return baseTTL * multiplier;
    }

    /**
     * Checks if the endless mode session is finished.
     *
     * The session ends when the player has no lives left.
     *
     * @param session current game session
     * @return true if lives are 0 or less, otherwise false
     */
    public boolean isSessionComplete(GameSession session) {
        return session.getLives() <= 0;
    }

}
