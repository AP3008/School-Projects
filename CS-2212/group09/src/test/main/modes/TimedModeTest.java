package main.modes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.engine.GameSession;
import main.gameplay.Word;

class TimedModeTest {

    @Test
    void getType_returnsTimed() {
        TimedMode mode = new TimedMode(60);
        assertEquals(ModeType.TIMED, mode.getType());
    }

    @Test
    void getInitialLives_returns100() {
        TimedMode mode = new TimedMode(60);
        assertEquals(100, mode.getInitialLives());
    }

    @Test
    void getSpawnRate_alwaysReturns2Point5() {
        TimedMode mode = new TimedMode(60);

        assertEquals(2.5, mode.getSpawnRate(1), 0.001);
        assertEquals(2.5, mode.getSpawnRate(5), 0.001);
        assertEquals(2.5, mode.getSpawnRate(10), 0.001);
    }

    @Test
    void getTargetTTL_scalesWithWordLength() {
        TimedMode mode = new TimedMode(60);
        Word shortWord = new Word("hi", Difficulty.EASY);
        Word longWord = new Word("encyclopedia", Difficulty.HARD);

        assertTrue(mode.getTargetTTL(longWord, 1) > mode.getTargetTTL(shortWord, 1));
    }

    @Test
    void getTargetTTL_usesMultiplierOf1Point2() {
        TimedMode mode = new TimedMode(60);
        Word word = new Word("hello", Difficulty.EASY); // length 5

        // baseTTL = 5 * 0.8 + 2.0 = 6.0, * 1.2 = 7.2
        double expected = (5 * 0.8 + 2.0) * 1.2;
        assertEquals(expected, mode.getTargetTTL(word, 1), 0.001);
    }

    @Test
    void onWordCorrect_doesNotChangeLives() {
        TimedMode mode = new TimedMode(60);
        GameSession session = new GameSession(null, ModeType.TIMED, 1);
        Word word = new Word("hi", Difficulty.EASY);

        int livesBefore = session.getLives();
        mode.onWordCorrect(session, word);

        assertEquals(livesBefore, session.getLives());
    }

    @Test
    void onWordIncorrect_doesNotChangeLives() {
        TimedMode mode = new TimedMode(60);
        GameSession session = new GameSession(null, ModeType.TIMED, 1);
        Word word = new Word("hi", Difficulty.EASY);

        int livesBefore = session.getLives();
        mode.onWordIncorrect(session, word);

        assertEquals(livesBefore, session.getLives());
    }

    @Test
    void isSessionComplete_falseBeforeDurationReached() {
        TimedMode mode = new TimedMode(60);
        GameSession session = new GameSession(null, ModeType.TIMED, 1);
        session.setElapsedTime(30000L); // 30 seconds

        assertFalse(mode.isSessionComplete(session));
    }

    @Test
    void isSessionComplete_trueAtExactDuration() {
        TimedMode mode = new TimedMode(60);
        GameSession session = new GameSession(null, ModeType.TIMED, 1);
        session.setElapsedTime(60000L); // exactly 60 seconds

        assertTrue(mode.isSessionComplete(session));
    }

    @Test
    void isSessionComplete_trueAfterDurationExceeded() {
        TimedMode mode = new TimedMode(60);
        GameSession session = new GameSession(null, ModeType.TIMED, 1);
        session.setElapsedTime(90000L); // 90 seconds

        assertTrue(mode.isSessionComplete(session));
    }
}
