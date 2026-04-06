package main.modes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.account.PlayerProfile;
import main.engine.GameSession;
import main.gameplay.Word;

class NormalModeTest {

    @Test
    void getType_returnsNormal() {
        NormalMode mode = new NormalMode(10, 2);
        assertEquals(ModeType.NORMAL, mode.getType());
    }

    @Test
    void getInitialLives_returns3() {
        NormalMode mode = new NormalMode(10, 2);
        assertEquals(3, mode.getInitialLives());
    }

    @Test
    void constructor_throwsForInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new NormalMode(0, 2));
        assertThrows(IllegalArgumentException.class, () -> new NormalMode(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> new NormalMode(10, -1));
    }

    @Test
    void getSpawnRate_decreasesWithLevel() {
        NormalMode mode = new NormalMode(10, 2);

        double level1Rate = mode.getSpawnRate(1);
        double level5Rate = mode.getSpawnRate(5);

        assertTrue(level1Rate > level5Rate);
    }

    @Test
    void getSpawnRate_floorIs1Point5() {
        NormalMode mode = new NormalMode(10, 2);

        // Level 10+ should be capped at minimum 1.5
        double highLevelRate = mode.getSpawnRate(100);

        assertEquals(1.5, highLevelRate, 0.001);
    }

    @Test
    void getTargetTTL_scalesWithWordLength() {
        NormalMode mode = new NormalMode(10, 2);
        Word shortWord = new Word("hi", Difficulty.EASY);
        Word longWord = new Word("extraordinary", Difficulty.HARD);

        double shortTTL = mode.getTargetTTL(shortWord, 1);
        double longTTL = mode.getTargetTTL(longWord, 1);

        assertTrue(longTTL > shortTTL);
    }

    @Test
    void getTargetTTL_isTighterAtHigherLevels() {
        NormalMode mode = new NormalMode(10, 2);
        Word word = new Word("hello", Difficulty.MEDIUM);

        double level1TTL = mode.getTargetTTL(word, 1);
        double level8TTL = mode.getTargetTTL(word, 8);

        assertTrue(level1TTL > level8TTL);
    }

    @Test
    void onWordIncorrect_losesLife() {
        NormalMode mode = new NormalMode(10, 2);
        GameSession session = new GameSession(null, ModeType.NORMAL, 1);
        Word word = new Word("hello", Difficulty.EASY);

        int livesBefore = session.getLives();
        mode.onWordIncorrect(session, word);

        assertEquals(livesBefore - 1, session.getLives());
    }

    @Test
    void onWordCorrect_advancesLevelWhenThresholdMet() {
        // Need a real PlayerProfile because NormalMode calls player.unlockLevel()
        PlayerProfile player = new PlayerProfile("alice", "pass");
        GameSession session = new GameSession(player, ModeType.NORMAL, 1);
        NormalMode mode = new NormalMode(3, 0); // threshold = 3 words
        Word word = new Word("hi", Difficulty.EASY);

        mode.onWordCorrect(session, word);
        mode.onWordCorrect(session, word);
        mode.onWordCorrect(session, word); // 3rd word triggers level advance

        assertEquals(2, session.getLevel());
    }

    @Test
    void isSessionComplete_trueWhenLivesAreZero() {
        NormalMode mode = new NormalMode(10, 2);
        GameSession session = new GameSession(null, ModeType.NORMAL, 1);
        session.setLives(0);

        assertTrue(mode.isSessionComplete(session));
    }

    @Test
    void isSessionComplete_falseWhenLivesRemain() {
        NormalMode mode = new NormalMode(10, 2);
        GameSession session = new GameSession(null, ModeType.NORMAL, 1);

        assertFalse(mode.isSessionComplete(session));
    }
}
