package main.engine;

import main.account.PlayerProfile;
import main.gameplay.TargetManager;
import main.gameplay.Word;
import main.gameplay.WordRepository;
import main.gameplay.WordTarget;
import main.modes.Difficulty;
import main.modes.EndlessMode;
import main.modes.GameMode;
import main.modes.ModeType;
import main.modes.NormalMode;
import main.modes.TimedMode;
import main.persistence.HighScoreEntry;
import main.persistence.HighScoreTable;
import main.persistence.PersistenceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Central game logic controller for KeyHunter.
 *
 * GameEngine coordinates all systems so a game session can be played.
 * The engine is driven entirely by GameScreen, which calls
 * {@link #handleKeystroke(char)} on every key event and {@link #update(double)} on
 * every timer tick. At the end of a session {@link #endSession()} packages results
 * into a {@link RunResult}, updates the player's {@link main.account.PlayerProfile},
 * and persists high scores through {@link PersistenceService}.
 * @author Adam Porbanderwalla
 */
public class GameEngine {

    private GameSession session;
    private GameMode mode;
    private TargetManager targetManager;
    private ScoreManager scoreManager;
    private AccuracyTracker accuracyTracker;
    private WordRepository wordRepository;
    private HighScoreTable highScoreTable;
    private PersistenceService persistence;
    private Random random;

    /**
     * Probability (7.5%) that a newly spawned duck carries a slowdown power up.
     */
    private static final double DOWN_CHANCE = 0.075;

    /**
     * Probability (5%) that a newly spawned target carries an extra life power up.
     */
    private static final double EXTRA_LIFE_CHANCE = 0.05;

    /**
     * Maximum number of lives a player may hold before extra life power ups stop spawning.
     * Set to 4 for fairness.
     */
    private static final int MAX_LIVES_FOR_EXTRA_LIFE = 4;

    /**
     * Constructs a new GameEngine. 
     *
     * Initialises a {@link ScoreManager}, {@link AccuracyTracker}, {@link TargetManager}, and a {@link Random} instance.
     * A session is not started until {@link #startNewSession(PlayerProfile, ModeType, int)} is called.
     *
     * @param wordRepository  the word source used to pull random words for targets
     * @param persistence     the service used to save high scores at session end
     * @param highScoreTable  the inmemory high score table to update and persist
     */
    public GameEngine(WordRepository wordRepository, PersistenceService persistence, HighScoreTable highScoreTable) {
        this.wordRepository = wordRepository;
        this.persistence = persistence;
        this.highScoreTable = highScoreTable;
        this.scoreManager = new ScoreManager(10, 5, false);
        this.accuracyTracker = new AccuracyTracker();
        this.targetManager = new TargetManager();
        this.random = new Random();
    }

    /**
     * Initialises and starts a new game session.
     *
     * Creates the appropriate {@link GameMode} for {@code modeType}, constructs
     * a {@link GameSession}, applies the mode's initial life count, and resets
     * {@link TargetManager}, {@link AccuracyTracker}, and {@link ScoreManager} to a
     * clean state. Called by GameScreen when the player begins a new game.
     *
     * @param player        the player whose profile will be updated at session end
     * @param modeType      the game mode to use.
     * @param startingLevel the level at which the session begins.
     */
    public void startNewSession(PlayerProfile player, ModeType modeType, int startingLevel) {
        this.mode = createMode(modeType);
        this.session = new GameSession(player, modeType, startingLevel);
        session.setLives(mode.getInitialLives());
        session.setState(GameState.RUNNING);
        targetManager.reset();
        accuracyTracker.reset();
        scoreManager.reset();
    }

    /**
     * Method that creates the {@link GameMode} implementation {@link ModeType}.
     *
     * @param modeType the requested mode
     * @return a configured {@link GameMode} instance
     */
    private GameMode createMode(ModeType modeType) {
        switch (modeType) {
            case NORMAL:
                return new NormalMode(10, 2);
            case ENDLESS:
                return new EndlessMode();
            case TIMED:
                return new TimedMode(60);
            default:
                return new NormalMode(10, 2);
        }
    }

    /**
     * Processes a single keystroke from the player during the game.
     *
     * Called by GameScreen on every key event. The keystroke is compared
     * against the expected next character of every active {@link main.gameplay.WordTarget}.
     * If a match is found the target's progress is tracked, if the target becomes
     * fully completed, the word is scored via {@link ScoreManager#applyCorrectWord(GameSession, Word)},
     * any power up on the target is activated, and the target is removed. If no
     * target matches, the keystroke is recorded as an error via
     * {@link AccuracyTracker#recordKeystroke(char, char)}.
     *
     * @param c the character typed by the player
     */
    public void handleKeystroke(char c) {
        if (session == null || session.getState() != GameState.RUNNING) return;

        List<WordTarget> targets = targetManager.getActiveTargets();
        for (WordTarget target : targets) {
            char expected = target.getWord().getText().charAt(target.getProgressIndex());
            if (expected == c) {
                accuracyTracker.recordKeystroke(expected, c);
                target.advanceProgress();
                if (target.isCompleted()) {
                    accuracyTracker.recordWordCorrect();
                    scoreManager.applyCorrectWord(session, target.getWord());
                    scoreManager.recordCorrectWord();
                    if (target.hasSlowdownPowerup()) {
                        scoreManager.activateSlowdown();
                    }
                    if (target.hasExtraLifePowerup()) {
                        session.gainLife();
                    }
                    mode.onWordCorrect(session, target.getWord());
                    targetManager.removeTarget(target);
                }
                return;
            }
        }

        accuracyTracker.recordKeystroke('\0', c);
    }

    /**
     * Advances the game simulation by one timer tick.
     *
     * Called by GameScreen on every Swing timer event. Handles: 
     * Accumulate elapsed time (halved in TIMED mode during slowdown), 
     * Advance all active target positions and power up timers, 
     * Penalise and remove any targets that have escaped the screen,
     * Spawn a new {@link main.gameplay.WordTarget} based on spawn rate interval
     * Check whether the mode signals the session is complete
     *
     * @param deltaSeconds seconds elapsed since the last tick (typically ~0.016 for 60 Hz)
     */
    public void update(double deltaSeconds) {
        if (session == null || session.getState() != GameState.RUNNING) return;

        double timeDelta = (scoreManager.isSlowdownActive() && session.getModeType() == ModeType.TIMED)
                ? deltaSeconds * 0.5 : deltaSeconds;
        long elapsed = session.getElapsedTime();
        session.setElapsedTime(elapsed + (long)(timeDelta * 1000));

        double targetDelta = scoreManager.isSlowdownActive() ? deltaSeconds * 0.5 : deltaSeconds;
        targetManager.update(targetDelta);
        scoreManager.update(deltaSeconds);

        List<WordTarget> escaped = new ArrayList<>();
        for (WordTarget target : targetManager.getActiveTargets()) {
            if (target.hasEscaped()) {
                escaped.add(target);
            }
        }
        for (WordTarget target : escaped) {
            scoreManager.applyMistake(session);
            scoreManager.resetStreak();
            mode.onWordIncorrect(session, target.getWord());
            targetManager.removeTarget(target);
        }

        if (targetManager.getTimeSinceLastSpawn() >= mode.getSpawnRate(session.getLevel())) {
            Difficulty diff = mode.getDifficultyForLevel(session.getLevel());
            Word word = wordRepository.getRandomWord(diff);
            if (word != null) {
                double speed = 1.0;
                double ttl = mode.getTargetTTL(word, session.getLevel());
                WordTarget newTarget = new WordTarget(word, speed, ttl);
                if (random.nextDouble() < DOWN_CHANCE) {
                    newTarget.setSlowdownPowerup(true);
                } else if (random.nextDouble() < EXTRA_LIFE_CHANCE) {
                    ModeType currentMode = session.getModeType();
                    if ((currentMode == ModeType.NORMAL || currentMode == ModeType.ENDLESS)
                            && session.getLives() < MAX_LIVES_FOR_EXTRA_LIFE) {
                        newTarget.setExtraLifePowerup(true);
                    }
                }
                targetManager.spawnTarget(newTarget);
            } else {
                targetManager.resetSpawnTimer();
            }
        }

        // Check if the mode says the session is over
        if (mode.isSessionComplete(session)) {
            session.setState(GameState.GAME_OVER);
            targetManager.clearAll();
        }
    }

    /**
     * Pauses the current session by transitioning its state to {@link GameState#PAUSED}.
     *
     * Called by {@link main.ui.screens.GameScreen} when the player pauses
     */
    public void pause() {
        if (session != null && session.getState() == GameState.RUNNING) {
            session.setState(GameState.PAUSED);
        }
    }

    /**
     * Resumes a paused session by transitioning its state back to {@link GameState#RUNNING}.
     *
     * Called by {@link main.ui.screens.GameScreen} when the player resumes the game
     */
    public void resume() {
        if (session != null && session.getState() == GameState.PAUSED) {
            session.setState(GameState.RUNNING);
        }
    }

    /**
     * Ends the current session, computes final statistics, updates the player profile,
     * records a highscore entry, and returns a {@link RunResult}.
     *
     * Sets session state to {@link GameState#GAME_OVER} and clear all targets.
     * Calculate WPM from {@link AccuracyTracker#getWordsCorrect()} and elapsed time.
     * {@link GameSession#createRunResult(double, AccuracyTracker)} builds the {@link RunResult}.
     * Calls updateStats() and updateExtendedStats() on {@link main.account.PlayerStats} to persist lifetime statistics.
     * Adds a {@link main.persistence.HighScoreEntry} to {@link HighScoreTable} and persist the table via {@link PersistenceService}.
     *
     * @return the {@link RunResult} for the completed session. 
     */
    public RunResult endSession() {
        if (session == null) return null;
        session.setState(GameState.GAME_OVER);
        targetManager.clearAll();
        double elapsedMinutes = session.getElapsedTime() / 60000.0;
        double wpm = (elapsedMinutes > 0) ? accuracyTracker.getWordsCorrect() / elapsedMinutes : 0;
        RunResult result = session.createRunResult(wpm, accuracyTracker);

        // Update player stats
        PlayerProfile player = session.getPlayer();
        if (player != null) {
            player.getStats().updateStats(
                result.getWpm(),
                result.getAccuracyPercent(),
                accuracyTracker.getWordsCorrect()
            );
            player.getStats().updateExtendedStats(
                accuracyTracker.getErrorCount(),
                session.getElapsedTime(),
                result.getScore(),
                result.getLevelReached(),
                session.getModeType()
            );
        }

        // Record high score
        if (highScoreTable != null && player != null) {
            HighScoreEntry entry = new HighScoreEntry(
                player.getUsername(),
                result.getScore(),
                session.getModeType(),
                System.currentTimeMillis()
            );
            highScoreTable.record(session.getModeType(), entry);
        }

        // Save high scores
        if (persistence != null && highScoreTable != null) {
            persistence.saveHighScores(highScoreTable);
        }

        return result;
    }

    /**
     * Saves the current highscore table to disk without ending the session.
     *
     * Delegates to {@link PersistenceService#saveHighScores(HighScoreTable)}.
     */
    public void saveProgress() {
        if (persistence != null && highScoreTable != null) {
            persistence.saveHighScores(highScoreTable);
        }
    }

    /**
     * {@link GameSession} getter method.
     *
     * Used by GameScreen to read HUD data (score, lives, level) and check for {@link GameState#GAME_OVER}.
     *
     * @return the current session, or null if none has been started
     */
    public GameSession getSession() {
        return session;
    }

    /**
     * Returns the list of active {@link main.gameplay.WordTarget} objects.
     *
     * Delegates to {@link TargetManager#getActiveTargets()}. Called by GameScreen each repaint cycle.
     *
     * @return a live list of active word targets
     */
    public List<WordTarget> getActiveTargets() {
        return targetManager.getActiveTargets();
    }

    /**
     * {@link AccuracyTracker} getter method.
     *
     * Exposed so GameScreen can display live accuracy on the HUD.
     *
     * @return the accuracy tracker owned by this engine
     */
    public AccuracyTracker getAccuracyTracker() {
        return accuracyTracker;
    }

    /**
     * {@link ScoreManager} getter method.
     *
     * Exposed so GameScreen can query powerup states for the HUD.
     *
     * @return the score manager owned by this engine
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * {@link main.modes.GameMode} getter method.
     *
     * Used by GameScreen to check modespecific display info.
     *
     * @return the current game mode, or null if no session has been started
     */
    public GameMode getMode() {
        return mode;
    }
}
