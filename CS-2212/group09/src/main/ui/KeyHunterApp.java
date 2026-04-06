package main.ui;

import main.account.AccountManager;
import main.account.ParentalControlService;
import main.engine.GameEngine;
import main.gameplay.WordRepository;
import main.persistence.HighScoreTable;
import main.persistence.PersistenceService;

import javax.swing.*;

/**
 * Application entry point for KeyHunter. Bootstraps all services and wires them through ScreenManager
 * before making the window visible. All screen navigation is handled inside ScreenManager.
 *
 * @author Adam Porbanderwalla, Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class KeyHunterApp {

    /** Default window width in pixels before the frame is maximised. */
    private static final int WINDOW_WIDTH = 1024;

    /** Default window height in pixels before the frame is maximised. */
    private static final int WINDOW_HEIGHT = 768;

    /** Title string displayed in the application window title bar. */
    private static final String TITLE = "KeyHunter";

    /**
     * Application entry point. Constructs all services in order (PersistenceService, HighScoreTable,
     * AccountManager, WordRepository, GameEngine, ParentalControlService, SoundManager), then creates
     * the JFrame and ScreenManager on the Swing event-dispatch thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        PersistenceService persistence = new PersistenceService("data/players.json", "data/highscores.json");
        HighScoreTable highScoreTable = persistence.loadHighScores();
        AccountManager accountManager = new AccountManager(persistence);
        WordRepository wordRepository = new WordRepository();
        GameEngine gameEngine = new GameEngine(wordRepository, persistence, highScoreTable);
        ParentalControlService parentalControl = new ParentalControlService("1234", accountManager, highScoreTable);
        SoundManager soundManager = new SoundManager();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setResizable(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            ScreenManager screenManager = new ScreenManager(
                frame, accountManager, gameEngine, highScoreTable,
                parentalControl, persistence, soundManager
            );

            frame.setContentPane(screenManager);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
