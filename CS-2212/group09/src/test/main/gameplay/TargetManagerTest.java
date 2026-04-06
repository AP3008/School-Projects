package main.gameplay;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.Difficulty;

class TargetManagerTest {

    @Test
    void constructor_setsDefault() {
        TargetManager manager = new TargetManager();

        assertTrue(manager.getActiveTargets().isEmpty());
        assertEquals(0, manager.getTimeSinceLastSpawn());
    }

    @Test
    void spawnTarget_addsTargetAndResetsSpawnTimer() {
        TargetManager manager = new TargetManager();
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 2.0);

        manager.update(5);
        manager.spawnTarget(target);

        assertTrue(manager.getActiveTargets().contains(target));
        assertEquals(0, manager.getTimeSinceLastSpawn());
    }

    @Test
    void removeTarget_removesTarget() {
        TargetManager manager = new TargetManager();
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 2.0);

        manager.spawnTarget(target);
        manager.removeTarget(target);

        assertFalse(manager.getActiveTargets().contains(target));
    }

    @Test
    void clearAll_removesAllTargets() {
        TargetManager manager = new TargetManager();
        Word word1 = new Word("a", Difficulty.EASY);
        Word word2 = new Word("b", Difficulty.EASY);
        WordTarget target1 = new WordTarget(word1, 1.0, 2.0);
        WordTarget target2 = new WordTarget(word2, 1.0, 2.0);

        manager.spawnTarget(target1);
        manager.spawnTarget(target2);
        manager.clearAll();

        assertTrue(manager.getActiveTargets().isEmpty());
    }

    @Test
    void reset_clearsTargetsAndResetsSpawnTimer() {
        TargetManager manager = new TargetManager();
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 2.0);

        manager.spawnTarget(target);
        manager.update(3.0);
        manager.reset();

        assertTrue(manager.getActiveTargets().isEmpty());
        assertEquals(0, manager.getTimeSinceLastSpawn());
    }

    @Test
    void update_increasesTimeSinceLastSpawn() {
        TargetManager manager = new TargetManager();

        manager.update(1.5);

        assertEquals(1.5, manager.getTimeSinceLastSpawn());
    }

    @Test
    void update_propagatesToActiveTargets() {
        TargetManager manager = new TargetManager();
        Word word = new Word("a", Difficulty.EASY);
        WordTarget target = new WordTarget(word, 1.0, 2.0);
        manager.spawnTarget(target);

        assertFalse(target.hasEscaped());

        manager.update(5.0); // exceeds ttl of 2.0

        assertTrue(target.hasEscaped());
    }

    @Test
    void resetSpawnTimer_setsTimerToZero() {
        TargetManager manager = new TargetManager();

        manager.update(3.0);
        manager.resetSpawnTimer();

        assertEquals(0, manager.getTimeSinceLastSpawn());
    }
}
