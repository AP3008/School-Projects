package main.engine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.Difficulty;
import main.modes.ModeType;
import main.gameplay.Word;
import main.gameplay.WordTarget;

class GameEngineTest {

    @Test
    void startNewSession_setsRunningState() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);
        assertEquals(GameState.RUNNING, engine.getSession().getState());
    }

    @Test
    void startsNewSession_setsCorrectInitialLives() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);
        assertEquals(3, engine.getSession().getLives());

        engine.startNewSession(null, ModeType.ENDLESS, 1);
        assertEquals(3, engine.getSession().getLives());

        engine.startNewSession(null, ModeType.TIMED, 1);
        assertEquals(100, engine.getSession().getLives());
    }

    @Test
    void startNewSession_createsCorrectMode() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);
        assertEquals(ModeType.NORMAL, engine.getMode().getType());

        engine.startNewSession(null, ModeType.ENDLESS, 1);
        assertEquals(ModeType.ENDLESS, engine.getMode().getType());

        engine.startNewSession(null, ModeType.TIMED, 1);
        assertEquals(ModeType.TIMED, engine.getMode().getType());
    }

    @Test
    void pause_whenRunning() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);

        engine.pause();

        assertEquals(GameState.PAUSED, engine.getSession().getState());
    }

    @Test
    void resume_whenPaused() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);

        engine.pause();
        engine.resume();

        assertEquals(GameState.RUNNING, engine.getSession().getState());
    }

    @Test
    void handleKeystroke_whenNotRunning_doesNothing() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);

        engine.pause();

        int initialWordsCorrect = engine.getAccuracyTracker().getWordsCorrect();
        double initialAccuracy = engine.getAccuracyTracker().getAccuracyPercent();

        engine.handleKeystroke('a');

        assertEquals(initialWordsCorrect, engine.getAccuracyTracker().getWordsCorrect());
        assertEquals(initialAccuracy, engine.getAccuracyTracker().getAccuracyPercent());
    }

    @Test
    void handleKeystroke_whenCorrect_advancesProgress() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);

        Word word = new Word("hi", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 10.0);

        engine.getActiveTargets().add(target);
        engine.handleKeystroke('h');
        assertEquals(1, target.getProgressIndex());
    }

    @Test
    void handleKeystroke_whenWordCompleted_removesTarget() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);

        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 10.0);

        engine.getActiveTargets().add(target);
        engine.handleKeystroke('a');
        assertFalse(engine.getActiveTargets().contains(target));
    }

    @Test
    void endSession_setsGameOverAndReturnsResult() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);
        RunResult result = engine.endSession();

        assertEquals(GameState.GAME_OVER, engine.getSession().getState());
        assertNotNull(result);
    }

    @Test
    void update_increasesElapsedTime() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);

        long elapsed = engine.getSession().getElapsedTime();
        engine.update(1);
        assertEquals(elapsed + 1000, engine.getSession().getElapsedTime());
    }

    @Test
    void update_whenTargetEscapes_losesLife() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.NORMAL, 1);

        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 0.01);

        engine.getActiveTargets().add(target);
        int lives = engine.getSession().getLives();
        engine.update(0.2);
        assertFalse(engine.getActiveTargets().contains(target));
        assertEquals(lives - 1, engine.getSession().getLives());
    }

    @Test
    void update_timedMode_sessionCompleteAfterTimeExpires() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.startNewSession(null, ModeType.TIMED, 1);

        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 10.0);
        engine.getActiveTargets().add(target);

        engine.getSession().setElapsedTime(100000);
        engine.update(0);

        assertEquals(GameState.GAME_OVER, engine.getSession().getState());
        assertTrue(engine.getActiveTargets().isEmpty());
    }
}
