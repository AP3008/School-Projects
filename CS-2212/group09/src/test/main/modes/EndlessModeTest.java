package main.modes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.engine.GameSession;
import main.gameplay.Word;

class EndlessModeTest {

    @Test
    void getType_returnsEndless() {
        EndlessMode mode = new EndlessMode();
        assertEquals(ModeType.ENDLESS, mode.getType());
    }

    @Test
    void getInitialLives_returns3() {
        EndlessMode mode = new EndlessMode();
        assertEquals(3, mode.getInitialLives());
    }

    @Test
    void getSpawnRate_initialRateIs3() {
        EndlessMode mode = new EndlessMode();
        assertEquals(3.0, mode.getSpawnRate(1), 0.001);
    }

    @Test
    void getSpawnRate_decreasesAsWordsClearedGrows() {
        EndlessMode mode = new EndlessMode();
        GameSession session = new GameSession(null, ModeType.ENDLESS, 1);
        Word word = new Word("hi", Difficulty.EASY);

        double rateBefore = mode.getSpawnRate(1);

        // Clear 30 words to reduce spawn rate
        for (int i = 0; i < 30; i++) {
            mode.onWordCorrect(session, word);
        }

        double rateAfter = mode.getSpawnRate(1);
        assertTrue(rateAfter < rateBefore);
    }

    @Test
    void getSpawnRate_floorIs0Point5() {
        EndlessMode mode = new EndlessMode();
        GameSession session = new GameSession(null, ModeType.ENDLESS, 1);
        Word word = new Word("hi", Difficulty.EASY);

        // Clear many words to drive rate to floor
        for (int i = 0; i < 1000; i++) {
            mode.onWordCorrect(session, word);
        }

        assertEquals(0.5, mode.getSpawnRate(1), 0.001);
    }

    @Test
    void getDifficultyForLevel_returnsValidDifficulty() {
        EndlessMode mode = new EndlessMode();

        for (int i = 0; i < 20; i++) {
            Difficulty d = mode.getDifficultyForLevel(1);
            assertNotNull(d);
            assertTrue(d == Difficulty.EASY || d == Difficulty.MEDIUM || d == Difficulty.HARD);
        }
    }

    @Test
    void onWordIncorrect_losesLife() {
        EndlessMode mode = new EndlessMode();
        GameSession session = new GameSession(null, ModeType.ENDLESS, 1);
        Word word = new Word("hi", Difficulty.EASY);

        int livesBefore = session.getLives();
        mode.onWordIncorrect(session, word);

        assertEquals(livesBefore - 1, session.getLives());
    }

    @Test
    void isSessionComplete_trueWhenLivesAreZero() {
        EndlessMode mode = new EndlessMode();
        GameSession session = new GameSession(null, ModeType.ENDLESS, 1);
        session.setLives(0);

        assertTrue(mode.isSessionComplete(session));
    }

    @Test
    void isSessionComplete_falseWhenLivesRemain() {
        EndlessMode mode = new EndlessMode();
        GameSession session = new GameSession(null, ModeType.ENDLESS, 1);

        assertFalse(mode.isSessionComplete(session));
    }
}
