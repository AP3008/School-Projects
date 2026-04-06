package main.persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.ModeType;

class HighScoreEntryTest {

    @Test
    void constructor_storesAllFields() {
        HighScoreEntry entry = new HighScoreEntry("alice", 500, ModeType.NORMAL, 1234567890L);

        assertEquals("alice", entry.getUsername());
        assertEquals(500, entry.getScore());
        assertEquals(ModeType.NORMAL, entry.getModeType());
        assertEquals(1234567890L, entry.getTimestampMillis());
    }

}
