package main.modes;

import main.engine.GameSession;
import main.gameplay.Word;
import java.util.Random;

/**
 * Represents the normal game mode in KeyHunter.
 *
 * In this mode, the player moves through levels by clearing enough
 * words in each level. As the level increases, the game becomes
 * harder through faster spawns, harder words, and shorter target time.
 * 
 * @author Imad Tahir
 * @see GameMode
 * 
 */
public class NormalMode implements GameMode {

    /** Tracks how many words have been typed correctly in the current level.*/
    private int wordsClearedThisLevel;

    /** The minimum number of words that must be cleared to complete level 1. */
    private int baseWordsRequired;

    /** Extra number of words required for each new level. */
    private int wordsIncrementPerLevel;

    /** Random object used to choose word difficulty. */

    private final Random random = new Random();

    /**
     * Creates a new NormalMode object.
     *
     * @param baseWordsRequired number of words needed for level 1
     * @param wordsIncrementPerLevel extra words required for each next level
     * @throws IllegalArgumentException if the values are invalid
     */
    public NormalMode(int baseWordsRequired, int wordsIncrementPerLevel) {
        if (baseWordsRequired <= 0 || wordsIncrementPerLevel < 0) {
            throw new IllegalArgumentException("baseWordsRequired must be > 0 and wordsIncrementPerLevel must be >= 0");
        }
        this.baseWordsRequired = baseWordsRequired;
        this.wordsIncrementPerLevel = wordsIncrementPerLevel;
        this.wordsClearedThisLevel = 0;
    }

    /**
     * Calculates the total number of words that must be cleared to complete the given level.
     *
     * The formula is baseWordsRequired + (level - 1) * wordsIncrementPerLevel,
     * so level 1 requires exactly baseWordsRequired words and each subsequent level has more words required
     *
     * @param level the level number
     * @return the word count required to advance from the given level to the next
     */
    private int getWordsRequiredForLevel(int level) {
        return baseWordsRequired + (level - 1) * wordsIncrementPerLevel;
    }

    /**
     * Returns the type of this mode.
     *
     * @return {@link ModeType#NORMAL}
     */
    public ModeType getType() {
        return ModeType.NORMAL;
    }

    /**
     * Returns the starting number of lives for normal mode.
     *
     * @return 3 lives
     */
    public int getInitialLives() {
        return 3;
    }

    /**
     * Returns the spawn rate for the current level.
     *
     * As the level increases, words spawn faster. The spawn rate
     * will not go below 1.5 seconds.
     *
     * @param level current level
     * @return spawn interval in seconds
     */
    public double getSpawnRate(int level) {
        double rate = 3.0 - (level - 1) * 0.15;
        return Math.max(rate, 1.5);
    }

/**
 * Chooses the difficulty of the next word based on the current level.
 *
 * The difficulty is selected using probabilities that change as the level increases.
 * At lower levels, most words are EASY. As the level goes up, the chance of EASY
 * words decreases, while MEDIUM and HARD words become more common.
 *
 * A random number from 0–99 is used to decide which difficulty to return
 * based on these calculated chances.
 *
 * @param level current level
 * @return the selected {@link Difficulty}
 */
    public Difficulty getDifficultyForLevel(int level) {
        int easyChance = Math.max(5, 100 - (level - 1) * 20);
        int hardChance = Math.min(20, Math.max(0, (level - 4) * 10));
        int roll = random.nextInt(100);
        if (roll < easyChance) {
            return Difficulty.EASY;
        } else if (roll < 100 - hardChance) {
            return Difficulty.MEDIUM;
        } else {
            return Difficulty.HARD;
        }
    }

    /**
     * Updates the mode when the player types a word correctly.
     *
     * The number of cleared words for the current level increases.
     * If the player has cleared enough words, the session moves to
     * the next level and unlocks it for the player if needed.
     *
     * @param session current game session
     * @param word word that was typed correctly
     */
    public void onWordCorrect(GameSession session, Word word) {
        wordsClearedThisLevel++;
        if (wordsClearedThisLevel >= getWordsRequiredForLevel(session.getLevel())) {
            session.advanceLevel();
            if (session.getLevel() <= 10) {
                session.getPlayer().unlockLevel(session.getLevel());
            }
            wordsClearedThisLevel = 0;
        }
    }

    /**
     * Updates the mode when the player misses a word.
     *
     * In normal mode, missing a word causes the player to lose a life.
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
     * The base time depends on the word length. Higher levels reduce
     * the multiplier, so the player gets less time to type the word.
     *
     * @param word the word being displayed
     * @param level current level
     * @return target TTL in seconds
     */
    public double getTargetTTL(Word word, int level) {
        double baseTTL = word.getText().length() * 0.8 + 2.0;
        double multiplier = Math.max(0.7, 1.5 - (level - 1) * 0.09);
        return baseTTL * multiplier;
    }

     /**
     * Checks if the normal mode session is finished.
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
