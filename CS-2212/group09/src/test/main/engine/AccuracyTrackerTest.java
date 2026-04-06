package main.engine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AccuracyTrackerTest {

    @Test
    void constructor_setsGoodDefaults() {
        AccuracyTracker tracker = new AccuracyTracker();

        assertEquals(0, tracker.getAccuracyPercent());
        assertEquals(0, tracker.getErrorCount());
        assertEquals(0, tracker.getWordsCorrect());
    }

    @Test
    void recordKeystroke_whenCorrect_increasesAccuracy() {
        AccuracyTracker tracker = new AccuracyTracker();

        tracker.recordKeystroke('a', 'a');

        assertEquals(100, tracker.getAccuracyPercent());
        assertEquals(0, tracker.getErrorCount());
    }

    @Test
    void recordKeystroke_whenIncorrect_increasesErrorCount() {
        AccuracyTracker tracker = new AccuracyTracker();

        tracker.recordKeystroke('a', 'b');

        assertEquals(0, tracker.getAccuracyPercent());
        assertEquals(1, tracker.getErrorCount());
    }

    @Test
    void recordKeystroke_calculates() {
        AccuracyTracker tracker = new AccuracyTracker();

        tracker.recordKeystroke('a', 'a');
        tracker.recordKeystroke('a', 'x');
        tracker.recordKeystroke('a', 'a');

        assertEquals(100.0 * 2 / 3, tracker.getAccuracyPercent(), 0.001);
        assertEquals(1, tracker.getErrorCount());
    }

    @Test
    void recordWordCorrect_increasesWordsCorrect() {
        AccuracyTracker tracker = new AccuracyTracker();

        tracker.recordWordCorrect();
        tracker.recordWordCorrect();

        assertEquals(2, tracker.getWordsCorrect());
    }

    @Test
    void reset_afterChanges_clearsAllTrackedValues() {
        AccuracyTracker tracker = new AccuracyTracker();

        tracker.recordKeystroke('a', 'a');
        tracker.recordKeystroke('a', 'x');
        tracker.recordWordCorrect();
        tracker.recordWordCorrect();

        tracker.reset();

        assertEquals(0, tracker.getAccuracyPercent());
        assertEquals(0, tracker.getErrorCount());
        assertEquals(0, tracker.getWordsCorrect());
    }
}
