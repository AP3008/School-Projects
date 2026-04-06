package main.modes;

import main.engine.GameSession;
import main.gameplay.Word;
import java.util.Random;

/**
 * Timed game mode for KeyHunter — player types as many words as possible in 60 seconds.
 * Missing a word doesn't cost a life, the game just ends when time runs out.
 *
 * @author Imad Tahir
 * @see GameMode
 */
public class TimedMode implements GameMode {

    /** How long the session lasts in seconds. */
    private int durationSeconds;

    /** Used to randomly pick word difficulty. */
    private final Random random = new Random();

    /**
     * Creates a new TimedMode with the given duration.
     *
     * @param durationSeconds how long the game lasts in seconds
     */
    public TimedMode(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    /**
     * Returns the type of this mode so the engine knows how to handle the session.
     *
     * @return {@link ModeType#TIMED} to identify this as a timed session
     */
    public ModeType getType() {
        return ModeType.TIMED;
    }

    /**
     * Returns a large dummy value so the engine's life-exhaustion check in
     * {@link #isSessionComplete} never triggers the session to end prematurely.
     *
     * @return 100, high enough that it will never be reached during a session
     */
    public int getInitialLives() {
        return 100;
    }

    /**
     * Always spawns words at a fixed rate regardless of level, keeping the pace predictable
     * so players can focus on accuracy rather than adapting to a changing speed.
     *
     * @param level ignored
     * @return 2.5 seconds between each word spawn
     */
    public double getSpawnRate(int level) {
        return 2.5;
    }

    /**
     * Picks a difficulty based on fixed probabilities: 35% Easy, 45% Medium, 20% Hard.
     * The result is passed to {@code WordRepository.getRandomWord()} to fetch a matching word.
     *
     * @param level ignored
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
     * Called when a word is typed correctly. ScoreManager handles points so nothing happens here.
     *
     * @param session the current game session
     * @param word the word that was typed correctly
     */
    public void onWordCorrect(GameSession session, Word word) {
        // Nothing — ScoreManager handles points
    }

     /**
     * Called when a word is missed. No penalty in timed mode so nothing happens here.
     *
     * @param session the current game session
     * @param word the word that was missed
     */
    public void onWordIncorrect(GameSession session, Word word) {
        // Nothing — no lives to lose in timed mode
        // Everything will be handled by scoring manager
    }

    
     /**
     * Calculates how long a word stays on screen based on its length, giving the player
     * enough time to type it without feeling rushed. Longer words get more time.
     *
     * @param word  the word being spawned
     * @param level ignored
     * @return TTL in seconds, scaled up by 1.2x to be generous since there's no life penalty
     */
    public double getTargetTTL(Word word, int level) {
        double baseTTL = word.getText().length() * 0.8 + 2.0;
        return baseTTL * 1.2;
    }

    /**
     * Checks whether the session's time limit has been reached by comparing elapsed milliseconds against the configured duration.
     *
     * @param session the current game session
     * @return true if the player has used up all their time, false if there's still time remaining
     */
    public boolean isSessionComplete(GameSession session) {
        return session.getElapsedTime() >= durationSeconds * 1000L;
    }

}
