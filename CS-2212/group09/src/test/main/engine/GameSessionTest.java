package main.engine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.ModeType;

class GameSessionTest {

    @Test
    void constructor_setsDefaultValuesCorrectly() {
        GameSession session = new GameSession(null, null, 5);

        assertEquals(5, session.getLevel());
        assertEquals(0, session.getScore());
        assertEquals(3, session.getLives());
        assertEquals(GameState.RUNNING, session.getState());
        assertNull(session.getPlayer());
        assertNull(session.getModeType());
    }

    @Test
    void addScore_increasesScore() {
        GameSession session = new GameSession(null, null, 1);

        session.addScore(10);
        session.addScore(5);

        assertEquals(15, session.getScore());
    }

    @Test
    void addScore_doesNotGoBelowZero() {
        GameSession session = new GameSession(null, null, 1);

        session.addScore(-100);

        assertEquals(0, session.getScore());
    }

    @Test
    void loseLife_decreasesLives() {
        GameSession session = new GameSession(null, null, 1);

        session.loseLife();

        assertEquals(2, session.getLives());
    }

    @Test
    void gainLife_increasesLives() {
        GameSession session = new GameSession(null, null, 1);

        session.gainLife();

        assertEquals(4, session.getLives());
    }

    @Test
    void advanceLevel_increasesLevelByOne() {
        GameSession session = new GameSession(null, null, 3);

        session.advanceLevel();

        assertEquals(4, session.getLevel());
    }

    @Test
    void reset_setsScoreBackToZero() {
        GameSession session = new GameSession(null, null, 1);

        session.addScore(25);
        session.reset();

        assertEquals(0, session.getScore());
    }

    @Test
    void setState_updatesState() {
        GameSession session = new GameSession(null, null, 1);

        session.setState(GameState.PAUSED);

        assertEquals(GameState.PAUSED, session.getState());
    }

    @Test
    void setLives_updatesLivesCorrectly() {
        GameSession session = new GameSession(null, null, 1);

        session.setLives(7);

        assertEquals(7, session.getLives());
    }
}
