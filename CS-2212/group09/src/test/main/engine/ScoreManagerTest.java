package main.engine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.gameplay.Word;
import main.modes.Difficulty;
import main.modes.ModeType;

class ScoreManagerTest {

    @Test
    void applyCorrectWord_addsNormalScore() {
        ScoreManager manager = new ScoreManager(10, -1, false);
        GameSession session = new GameSession(null, ModeType.NORMAL, 1);
        Word word = new Word("hat", Difficulty.EASY);

        manager.applyCorrectWord(session, word);

        assertEquals(30, session.getScore());
    }

    @Test
    void applyMistake_addsPenaltyToScore() {
        ScoreManager manager = new ScoreManager(10, -1, false);
        GameSession session = new GameSession(null, ModeType.NORMAL, 1);

        session.addScore(20);
        manager.applyMistake(session);

        assertEquals(19, session.getScore());
    }

    @Test
    void recordCorrectWord_increasesStreak() {
        ScoreManager manager = new ScoreManager(10, -1, false);

        manager.recordCorrectWord();
        manager.recordCorrectWord();

        assertEquals(2, manager.getStreak());
    }

    @Test
    void recordCorrectWord_atThreshold_activatesDoublePointsAndResetsStreak() {
        ScoreManager manager = new ScoreManager(10, -5, false);

        for (int i = 0; i < 10; i++) {
            manager.recordCorrectWord();
        }

        assertTrue(manager.isDoublePointsActive());
        assertEquals(0, manager.getStreak());
    }

    @Test
    void resetStreak_setsStreakToZero() {
        ScoreManager manager = new ScoreManager(10, -5, false);

        manager.recordCorrectWord();
        manager.recordCorrectWord();
        manager.resetStreak();

        assertEquals(0, manager.getStreak());
    }

    @Test
    void activateSlowdown_setsSlowdownActive() {
        ScoreManager manager = new ScoreManager(10, -5, false);

        manager.activateSlowdown();

        assertTrue(manager.isSlowdownActive());
    }

    @Test
    void update_whenSlowdownExpires_turnsItOff() {
        ScoreManager manager = new ScoreManager(10, -5, false);

        manager.activateSlowdown();
        manager.update(6.0); // slowdown lasts 5s

        assertFalse(manager.isSlowdownActive());
    }

    @Test
    void reset_clearsDoublePointsAndStreak() {
        ScoreManager manager = new ScoreManager(10, -5, false);

        manager.activateSlowdown();
        manager.recordCorrectWord();
        manager.reset();

        assertFalse(manager.isDoublePointsActive());
        assertFalse(manager.isSlowdownActive());
        assertEquals(0, manager.getStreak());
    }
}
