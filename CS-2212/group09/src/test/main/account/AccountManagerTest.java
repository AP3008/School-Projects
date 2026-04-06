package main.account;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import main.persistence.PersistenceService;

import java.io.File;
import java.io.IOException;

class AccountManagerTest {

    private File playersFile;
    private File highScoresFile;
    private AccountManager manager;

    @BeforeEach
    void setUp() throws IOException {
        playersFile = File.createTempFile("players", ".json");
        highScoresFile = File.createTempFile("highscores", ".json");
        PersistenceService persistence = new PersistenceService(
                playersFile.getAbsolutePath(),
                highScoresFile.getAbsolutePath());
        manager = new AccountManager(persistence);
    }

    @AfterEach
    void tearDown() {
        playersFile.delete();
        highScoresFile.delete();
    }

    @Test
    void createAccount_succeeds_withValidCredentials() {
        boolean result = manager.createAccount("alice", "pass123");

        assertTrue(result);
        assertNotNull(manager.findPlayer("alice"));
    }

    @Test
    void createAccount_fails_forDuplicateUsername() {
        manager.createAccount("alice", "pass1");
        boolean result = manager.createAccount("alice", "pass2");

        assertFalse(result);
    }

    @Test
    void createAccount_fails_forNullUsername() {
        boolean result = manager.createAccount(null, "pass");

        assertFalse(result);
    }

    @Test
    void createAccount_fails_forBlankUsername() {
        boolean result = manager.createAccount("   ", "pass");

        assertFalse(result);
    }

    @Test
    void createAccount_fails_forBlankPassword() {
        boolean result = manager.createAccount("bob", "  ");

        assertFalse(result);
    }

    @Test
    void login_succeeds_withCorrectCredentials() {
        manager.createAccount("alice", "secret");

        boolean result = manager.login("alice", "secret");

        assertTrue(result);
        assertNotNull(manager.getCurrentPlayer());
        assertEquals("alice", manager.getCurrentPlayer().getUsername());
    }

    @Test
    void login_fails_withWrongPassword() {
        manager.createAccount("alice", "secret");

        boolean result = manager.login("alice", "wrong");

        assertFalse(result);
        assertNull(manager.getCurrentPlayer());
    }

    @Test
    void login_fails_forUnknownUser() {
        boolean result = manager.login("nobody", "pass");

        assertFalse(result);
    }

    @Test
    void findPlayer_returnsProfileForKnownUser() {
        manager.createAccount("alice", "pass");

        PlayerProfile profile = manager.findPlayer("alice");

        assertNotNull(profile);
        assertEquals("alice", profile.getUsername());
    }

    @Test
    void findPlayer_returnsNullForUnknownUser() {
        assertNull(manager.findPlayer("unknown"));
    }

    @Test
    void resetPassword_updatesPassword() {
        manager.createAccount("alice", "old");

        manager.resetPassword("alice", "new");

        assertEquals("new", manager.findPlayer("alice").getPassword());
    }

    @Test
    void resetPassword_noOpForUnknownUser() {
        manager.resetPassword("nobody", "new");
    }

    @Test
    void logout_clearsCurrentPlayer() {
        manager.createAccount("alice", "pass");
        manager.login("alice", "pass");

        manager.logout();

        assertNull(manager.getCurrentPlayer());
    }
}
