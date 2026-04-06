package main.persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import main.account.PlayerProfile;
import main.account.PlayerStats;
import main.account.Settings;
import main.modes.ModeType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class PersistenceServiceTest {

    private File playersFile;
    private File highScoresFile;
    private PersistenceService service;

    @BeforeEach
    void setUp() throws IOException {
        playersFile = File.createTempFile("players_test", ".json");
        highScoresFile = File.createTempFile("highscores_test", ".json");
        service = new PersistenceService(
                playersFile.getAbsolutePath(),
                highScoresFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        playersFile.delete();
        highScoresFile.delete();
    }

    @Test
    void loadPlayers_onEmptyFile_returnsEmptyList() {
        List<PlayerProfile> players = service.loadPlayers();

        assertTrue(players.isEmpty());
    }

    @Test
    void savePlayers_andLoadPlayers_preservesUsernameAndPassword() {
        PlayerProfile profile = new PlayerProfile("alice", "secret");
        service.savePlayers(Arrays.asList(profile));

        List<PlayerProfile> loaded = service.loadPlayers();

        assertEquals(1, loaded.size());
        assertEquals("alice", loaded.get(0).getUsername());
        assertEquals("secret", loaded.get(0).getPassword());
    }

    @Test
    void savePlayers_andLoadPlayers_preservesStats() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");
        profile.getStats().updateStats(55.0, 92.0, 15);
        profile.getStats().updateExtendedStats(3, 60000L, 400, 5, ModeType.NORMAL);
        service.savePlayers(Arrays.asList(profile));

        List<PlayerProfile> loaded = service.loadPlayers();
        PlayerStats stats = loaded.get(0).getStats();

        assertEquals(55.0, stats.getAverageWPM(), 0.001);
        assertEquals(55.0, stats.getPeakWPM(), 0.001);
        assertEquals(92.0, stats.getOverallAccuracy(), 0.001);
        assertEquals(1, stats.getTotalSessions());
        assertEquals(15, stats.getTotalWordsCorrect());
        assertEquals(3, stats.getTotalErrors());
        assertEquals(60000L, stats.getTotalTimePlayed());
        assertEquals(400, stats.getHighScore());
        assertEquals(400, stats.getHighScoreNormal());
        assertEquals(0, stats.getHighScoreTimed());
        assertEquals(0, stats.getHighScoreEndless());
        assertEquals(5, stats.getHighestLevel());
    }

    @Test
    void savePlayers_andLoadPlayers_preservesUnlockedLevels() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");
        profile.unlockLevel(2);
        profile.unlockLevel(3);
        service.savePlayers(Arrays.asList(profile));

        List<PlayerProfile> loaded = service.loadPlayers();

        assertTrue(loaded.get(0).isLevelUnlocked(1));
        assertTrue(loaded.get(0).isLevelUnlocked(2));
        assertTrue(loaded.get(0).isLevelUnlocked(3));
    }

    @Test
    void savePlayers_andLoadPlayers_preservesSettings() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");
        profile.setSettings(new Settings(7, false, true));
        service.savePlayers(Arrays.asList(profile));

        List<PlayerProfile> loaded = service.loadPlayers();
        Settings settings = loaded.get(0).getSettings();

        assertEquals(7, settings.getVolume());
        assertFalse(settings.isMusicEnabled());
        assertTrue(settings.isSoundEffectsEnabled());
    }

    @Test
    void loadHighScores_onEmptyFile_returnsEmptyTable() {
        HighScoreTable table = service.loadHighScores();

        assertEquals(0, table.getAllEntries().size());
    }

    @Test
    void saveHighScores_andLoadHighScores_preservesAllFields() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL,
                new HighScoreEntry("alice", 750, ModeType.NORMAL, 9999L));
        service.saveHighScores(table);

        HighScoreTable loaded = service.loadHighScores();
        HighScoreEntry[] top = loaded.getTop(ModeType.NORMAL, 10);

        assertEquals(1, top.length);
        assertEquals("alice", top[0].getUsername());
        assertEquals(750, top[0].getScore());
        assertEquals(ModeType.NORMAL, top[0].getModeType());
        assertEquals(9999L, top[0].getTimestampMillis());
    }

    @Test
    void saveHighScores_andLoadHighScores_preservesMultipleEntries() {
        HighScoreTable table = new HighScoreTable();
        table.record(ModeType.NORMAL,
                new HighScoreEntry("alice", 300, ModeType.NORMAL, 0L));
        table.record(ModeType.TIMED,
                new HighScoreEntry("bob", 500, ModeType.TIMED, 0L));
        service.saveHighScores(table);

        HighScoreTable loaded = service.loadHighScores();

        assertEquals(2, loaded.getAllEntries().size());
        assertEquals(1, loaded.getTop(ModeType.NORMAL, 10).length);
        assertEquals(1, loaded.getTop(ModeType.TIMED, 10).length);
    }
}
