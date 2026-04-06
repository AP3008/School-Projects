package main.account;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.ModeType;

class PlayerStatsTest {

    @Test
    void constructor_initializesAllFieldsToDefaults() {
        PlayerStats stats = new PlayerStats();

        assertEquals(0.0, stats.getAverageWPM());
        assertEquals(0.0, stats.getPeakWPM());
        assertEquals(0.0, stats.getOverallAccuracy());
        assertEquals(0, stats.getTotalWordsCorrect());
        assertEquals(0, stats.getTotalSessions());
        assertEquals(0, stats.getTotalErrors());
        assertEquals(0L, stats.getTotalTimePlayed());
        assertEquals(0, stats.getHighScore());
        assertEquals(0, stats.getHighScoreNormal());
        assertEquals(0, stats.getHighScoreTimed());
        assertEquals(0, stats.getHighScoreEndless());
        assertEquals(1, stats.getHighestLevel()); // starts at 1
    }

    @Test
    void updateStats_incrementsSessionCount() {
        PlayerStats stats = new PlayerStats();

        stats.updateStats(50.0, 95.0, 10);

        assertEquals(1, stats.getTotalSessions());
    }

    @Test
    void updateStats_updatesPeakWPM() {
        PlayerStats stats = new PlayerStats();

        stats.updateStats(40.0, 90.0, 5);
        stats.updateStats(60.0, 85.0, 8);

        assertEquals(60.0, stats.getPeakWPM());
    }

    @Test
    void updateStats_doesNotLowerPeakWPM() {
        PlayerStats stats = new PlayerStats();

        stats.updateStats(80.0, 90.0, 10);
        stats.updateStats(30.0, 70.0, 5);

        assertEquals(80.0, stats.getPeakWPM());
    }

    @Test
    void updateStats_calculatesRunningAverageWPM() {
        PlayerStats stats = new PlayerStats();

        stats.updateStats(40.0, 90.0, 5);
        stats.updateStats(60.0, 80.0, 5);

        assertEquals(50.0, stats.getAverageWPM(), 0.001);
    }

    @Test
    void updateStats_accumulatesWordsCorrect() {
        PlayerStats stats = new PlayerStats();

        stats.updateStats(50.0, 90.0, 7);
        stats.updateStats(50.0, 90.0, 3);

        assertEquals(10, stats.getTotalWordsCorrect());
    }

    @Test
    void updateExtendedStats_withModeType_updatesNormalHighScore() {
        PlayerStats stats = new PlayerStats();

        stats.updateExtendedStats(2, 60000L, 300, 3, ModeType.NORMAL);

        assertEquals(300, stats.getHighScoreNormal());
        assertEquals(0, stats.getHighScoreTimed());
        assertEquals(0, stats.getHighScoreEndless());
    }

    @Test
    void updateExtendedStats_withModeType_updatesTimedHighScore() {
        PlayerStats stats = new PlayerStats();

        stats.updateExtendedStats(1, 60000L, 250, 1, ModeType.TIMED);

        assertEquals(250, stats.getHighScoreTimed());
    }

    @Test
    void updateExtendedStats_withModeType_updatesEndlessHighScore() {
        PlayerStats stats = new PlayerStats();

        stats.updateExtendedStats(0, 120000L, 400, 1, ModeType.ENDLESS);

        assertEquals(400, stats.getHighScoreEndless());
    }

    @Test
    void updateExtendedStats_doesNotLowerExistingHighScore() {
        PlayerStats stats = new PlayerStats();

        stats.updateExtendedStats(0, 60000L, 500, 5, ModeType.NORMAL);
        stats.updateExtendedStats(0, 60000L, 200, 3, ModeType.NORMAL);

        assertEquals(500, stats.getHighScoreNormal());
    }

    @Test
    void updateExtendedStats_updatesHighestLevel() {
        PlayerStats stats = new PlayerStats();

        stats.updateExtendedStats(0, 0L, 0, 7, ModeType.NORMAL);

        assertEquals(7, stats.getHighestLevel());
    }

    @Test
    void updateExtendedStats_highestLevelCappedAt10() {
        PlayerStats stats = new PlayerStats();

        stats.updateExtendedStats(0, 0L, 0, 15, ModeType.NORMAL);

        assertEquals(10, stats.getHighestLevel());
    }

    @Test
    void getHighScoreForMode_returnsCorrectValue() {
        PlayerStats stats = new PlayerStats();
        stats.setHighScoreNormal(100);
        stats.setHighScoreTimed(200);
        stats.setHighScoreEndless(300);

        assertEquals(100, stats.getHighScoreForMode(ModeType.NORMAL));
        assertEquals(200, stats.getHighScoreForMode(ModeType.TIMED));
        assertEquals(300, stats.getHighScoreForMode(ModeType.ENDLESS));
    }

    @Test
    void resetStats_clearsAllFieldsToDefaults() {
        PlayerStats stats = new PlayerStats();
        stats.updateStats(60.0, 95.0, 20);
        stats.updateExtendedStats(5, 120000L, 800, 8, ModeType.NORMAL);

        stats.resetStats();

        assertEquals(0.0, stats.getAverageWPM());
        assertEquals(0.0, stats.getPeakWPM());
        assertEquals(0, stats.getTotalSessions());
        assertEquals(0, stats.getTotalWordsCorrect());
        assertEquals(0, stats.getHighScore());
        assertEquals(0, stats.getHighScoreNormal());
        assertEquals(1, stats.getHighestLevel());
    }
}
