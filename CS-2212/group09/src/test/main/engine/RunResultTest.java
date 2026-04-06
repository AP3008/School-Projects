package main.engine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.ModeType;

class RunResultTest {

    @Test
    void constructor_storesAllFields() {
        RunResult result = new RunResult(ModeType.NORMAL, 500, 45.5, 92.3, 3, 120, 4);

        assertEquals(ModeType.NORMAL, result.getMode());
        assertEquals(500, result.getScore());
        assertEquals(45.5, result.getWpm());
        assertEquals(92.3, result.getAccuracyPercent());
        assertEquals(3, result.getErrors());
        assertEquals(120, result.getDurationInSeconds());
        assertEquals(4, result.getLevelReached());
    }

}
