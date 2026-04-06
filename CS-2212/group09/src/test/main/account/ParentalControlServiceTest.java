package main.account;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import main.persistence.HighScoreEntry;
import main.persistence.HighScoreTable;
import main.persistence.PersistenceService;
import main.modes.ModeType;

import java.io.File;
import java.io.IOException;

class ParentalControlServiceTest {

    private File playersFile;
    private File highScoresFile;
    private AccountManager accountManager;
    private HighScoreTable highScoreTable;
    private ParentalControlService service;

    @BeforeEach
    void setUp() throws IOException {
        playersFile = File.createTempFile("players", ".json");
        highScoresFile = File.createTempFile("highscores", ".json");
        PersistenceService persistence = new PersistenceService(
                playersFile.getAbsolutePath(),
                highScoresFile.getAbsolutePath());
        accountManager = new AccountManager(persistence);
        highScoreTable = new HighScoreTable();
        service = new ParentalControlService("1234", accountManager, highScoreTable);
    }

    @AfterEach
    void tearDown() {
        playersFile.delete();
        highScoresFile.delete();
    }

    @Test
    void verifyPin_correctPin_returnsTrue() {
        assertTrue(service.verifyPin("1234"));
    }

    @Test
    void verifyPin_wrongPin_returnsFalse() {
        assertFalse(service.verifyPin("0000"));
    }

    @Test
    void viewPlayerStats_returnsStatsForKnownPlayer() {
        accountManager.createAccount("alice", "pass");

        PlayerStats stats = service.viewPlayerStats("alice");

        assertNotNull(stats);
    }

    @Test
    void viewPlayerStats_returnsNullForUnknownPlayer() {
        assertNull(service.viewPlayerStats("nobody"));
    }

    @Test
    void resetPlayerStats_zerosAllStats() {
        accountManager.createAccount("alice", "pass");
        accountManager.findPlayer("alice").getStats().updateStats(60.0, 95.0, 20);

        service.resetPlayerStats("alice");

        PlayerStats stats = accountManager.findPlayer("alice").getStats();
        assertEquals(0.0, stats.getAverageWPM());
        assertEquals(0, stats.getTotalSessions());
        assertEquals(0, stats.getTotalWordsCorrect());
    }

    @Test
    void resetHighScores_clearsAllEntries() {
        highScoreTable.record(ModeType.NORMAL,
                new HighScoreEntry("alice", 500, ModeType.NORMAL, 0L));
        highScoreTable.record(ModeType.TIMED,
                new HighScoreEntry("bob", 300, ModeType.TIMED, 0L));

        service.resetHighScores();

        assertEquals(0, highScoreTable.getAllEntries().size());
    }

    @Test
    void resetPassword_delegatesToAccountManager() {
        accountManager.createAccount("alice", "old");

        service.resetPassword("alice", "new");

        assertEquals("new", accountManager.findPlayer("alice").getPassword());
    }
}
