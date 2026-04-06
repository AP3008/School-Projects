package main.gameplay;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.Difficulty;

class WordTargetTest {

    @Test
    void constructor_setsDefaults() {
        Word word = new Word("hi", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.5, 5.0);

        assertEquals(0, target.getProgressIndex());
        assertFalse(target.hasEscaped());
        assertFalse(target.hasSlowdownPowerup());
        assertFalse(target.hasExtraLifePowerup());
        assertEquals(5.0, target.getTtl());
        assertEquals(word, target.getWord());
    }

    @Test
    void advanceProgress_incrementsIndex() {
        Word word = new Word("abc", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 5.0);

        target.advanceProgress();
        assertEquals(1, target.getProgressIndex());

        target.advanceProgress();
        assertEquals(2, target.getProgressIndex());
    }

    @Test
    void advanceProgress_doesNotExceedWordLength() {
        Word word = new Word("ab", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 5.0);

        target.advanceProgress();
        target.advanceProgress();
        target.advanceProgress(); // extra call — should not go past 2

        assertEquals(2, target.getProgressIndex());
    }

    @Test
    void isCompleted_falseWhenNotDone() {
        Word word = new Word("abc", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 5.0);

        target.advanceProgress();

        assertFalse(target.isCompleted());
    }

    @Test
    void isCompleted_trueWhenFullyTyped() {
        Word word = new Word("ab", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 5.0);

        target.advanceProgress();
        target.advanceProgress();

        assertTrue(target.isCompleted());
    }

    @Test
    void update_marksEscapedAfterTtlExpires() {
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 2.0);

        target.update(2.1);

        assertTrue(target.hasEscaped());
    }

    @Test
    void update_doesNotEscapeBeforeTtl() {
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 5.0);

        target.update(1.0);

        assertFalse(target.hasEscaped());
    }

    @Test
    void update_accumulatesTimeAlive() {
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 10.0);

        target.update(1.0);
        target.update(2.0);

        assertEquals(3.0, target.getTimeAlive());
    }

    @Test
    void slowdownPowerup_setAndGet() {
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 5.0);

        target.setSlowdownPowerup(true);
        assertTrue(target.hasSlowdownPowerup());

        target.setSlowdownPowerup(false);
        assertFalse(target.hasSlowdownPowerup());
    }

    @Test
    void extraLifePowerup_setAndGet() {
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 5.0);

        target.setExtraLifePowerup(true);
        assertTrue(target.hasExtraLifePowerup());

        target.setExtraLifePowerup(false);
        assertFalse(target.hasExtraLifePowerup());
    }
}
