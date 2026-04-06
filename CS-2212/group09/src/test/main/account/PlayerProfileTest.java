package main.account;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PlayerProfileTest {

    @Test
    void constructor_storesUsernameAndPassword() {
        PlayerProfile profile = new PlayerProfile("alice", "pass123");

        assertEquals("alice", profile.getUsername());
        assertEquals("pass123", profile.getPassword());
    }

    @Test
    void constructor_unlocksLevel1ByDefault() {
        PlayerProfile profile = new PlayerProfile("alice", "pass123");

        assertTrue(profile.isLevelUnlocked(1));
        assertFalse(profile.isLevelUnlocked(2));
    }

    @Test
    void unlockLevel_addsNewLevel() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");

        profile.unlockLevel(3);

        assertTrue(profile.isLevelUnlocked(3));
    }

    @Test
    void unlockLevel_doesNotDuplicateExistingLevel() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");

        profile.unlockLevel(2);
        profile.unlockLevel(2);

        // Should still only appear once — verify it's unlocked but list has no duplicates
        assertTrue(profile.isLevelUnlocked(2));
        assertEquals(1, profile.getUnlockedLevels().stream()
                .filter(l -> l == 2).count());
    }

    @Test
    void unlockLevel_ignoresOutOfRangeLevels() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");

        profile.unlockLevel(0);
        profile.unlockLevel(11);

        assertFalse(profile.isLevelUnlocked(0));
        assertFalse(profile.isLevelUnlocked(11));
    }

    @Test
    void unlockLevel_allowsMaxLevel10() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");

        profile.unlockLevel(10);

        assertTrue(profile.isLevelUnlocked(10));
    }

    @Test
    void getStats_neverReturnsNull() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");

        assertNotNull(profile.getStats());
    }

    @Test
    void getSettings_neverReturnsNull() {
        PlayerProfile profile = new PlayerProfile("alice", "pass");
        profile.setSettings(null);

        assertNotNull(profile.getSettings());
    }
}
