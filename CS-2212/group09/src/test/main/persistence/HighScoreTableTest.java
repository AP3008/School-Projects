package main.persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.ModeType;

class HighScoreTableTest {

    @Test
    void record_addsNewEntry() {
        HighScoreTable table = new HighScoreTable();
        HighScoreEntry entry = new HighScoreEntry("alice", 500, ModeType.NORMAL, 0L);

        table.record(ModeType.NORMAL, entry);

        assertEquals(1, table.getAllEntries().size());
    }

    @Test
    void record_replacesExistingEntry_whenNewScoreIsHigher() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 300, ModeType.NORMAL, 0L));
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 500, ModeType.NORMAL, 0L));

        HighScoreEntry[] top = table.getTop(ModeType.NORMAL, 10);
        assertEquals(1, top.length);
        assertEquals(500, top[0].getScore());
    }

    @Test
    void record_keepsExistingEntry_whenNewScoreIsLower() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 500, ModeType.NORMAL, 0L));
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 200, ModeType.NORMAL, 0L));

        HighScoreEntry[] top = table.getTop(ModeType.NORMAL, 10);
        assertEquals(1, top.length);
        assertEquals(500, top[0].getScore());
    }

    @Test
    void record_keepsBothEntries_whenDifferentMode() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 300, ModeType.NORMAL, 0L));
        table.record(ModeType.TIMED, new HighScoreEntry("alice", 400, ModeType.TIMED, 0L));

        assertEquals(2, table.getAllEntries().size());
    }

    @Test
    void getTop_returnsSortedHighestFirst() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 100, ModeType.NORMAL, 0L));
        table.record(ModeType.NORMAL, new HighScoreEntry("bob", 300, ModeType.NORMAL, 0L));
        table.record(ModeType.NORMAL, new HighScoreEntry("charlie", 200, ModeType.NORMAL, 0L));

        HighScoreEntry[] top = table.getTop(ModeType.NORMAL, 10);

        assertEquals(300, top[0].getScore());
        assertEquals(200, top[1].getScore());
        assertEquals(100, top[2].getScore());
    }

    @Test
    void getTop_respectsLimit() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 100, ModeType.NORMAL, 0L));
        table.record(ModeType.NORMAL, new HighScoreEntry("bob", 200, ModeType.NORMAL, 0L));
        table.record(ModeType.NORMAL, new HighScoreEntry("charlie", 300, ModeType.NORMAL, 0L));

        HighScoreEntry[] top = table.getTop(ModeType.NORMAL, 2);

        assertEquals(2, top.length);
        assertEquals(300, top[0].getScore());
        assertEquals(200, top[1].getScore());
    }

    @Test
    void getTop_returnsEmptyArray_whenNoEntriesForMode() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 100, ModeType.NORMAL, 0L));

        HighScoreEntry[] top = table.getTop(ModeType.TIMED, 10);

        assertEquals(0, top.length);
    }

    @Test
    void reset_clearsAllEntries() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 100, ModeType.NORMAL, 0L));
        table.record(ModeType.TIMED, new HighScoreEntry("bob", 200, ModeType.TIMED, 0L));

        table.reset();

        assertEquals(0, table.getAllEntries().size());
    }

    @Test
    void resetForPlayer_removesOnlyThatPlayersEntries() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL, new HighScoreEntry("alice", 300, ModeType.NORMAL, 0L));
        table.record(ModeType.TIMED, new HighScoreEntry("alice", 400, ModeType.TIMED, 0L));
        table.record(ModeType.NORMAL, new HighScoreEntry("bob", 500, ModeType.NORMAL, 0L));

        table.resetForPlayer("alice");

        assertEquals(1, table.getAllEntries().size());
        assertEquals("bob", table.getAllEntries().get(0).getUsername());
    }
}
