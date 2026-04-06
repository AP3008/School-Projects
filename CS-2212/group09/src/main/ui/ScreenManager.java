package main.ui;

import main.account.AccountManager;
import main.account.ParentalControlService;
import main.engine.GameEngine;
import main.engine.RunResult;
import main.modes.ModeType;
import main.persistence.HighScoreTable;
import main.persistence.PersistenceService;
import main.ui.screens.*;

import javax.swing.*;
import java.awt.*;

/**
 * Central UI controller for KeyHunter. Uses CardLayout to manage all screens and owns all
 * shared services (GameEngine, AccountManager, PersistenceService, HighScoreTable, SoundManager).
 * Created by KeyHunterApp and used by every screen for navigation and service access.
 *
 * Artificial Intelligence Tool: Claude; Execution: Used it to implement the constructor;
 * @author Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ScreenManager extends JPanel {

    private CardLayout cardLayout;
    private JFrame frame;
    private AccountManager accountManager;
    private GameEngine gameEngine;
    private HighScoreTable highScoreTable;
    private ParentalControlService parentalControlService;
    private PersistenceService persistence;
    private SoundManager soundManager;

    private MainMenuScreen mainMenuScreen;
    private LoginScreen loginScreen;
    private PlayerScreen playerScreen;
    private GameModeSelectScreen gameModeSelectScreen;
    private LevelSelectScreen levelSelectScreen;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private HighScoreScreen highScoreScreen;
    private TutorialScreen tutorialScreen;
    private PlayerStatsScreen playerStatsScreen;
    private ParentalPinScreen parentalPinScreen;
    private ParentalControlScreen parentalControlScreen;
    private ResetMenuScreen resetMenuScreen;
    private CreateChildAccountScreen createChildAccountScreen;
    private ResetPasswordParentalScreen resetPasswordParentalScreen;
    private ResetHighScoresScreen resetHighScoresScreen;
    private ResetStatsScreen resetStatsScreen;
    private ViewPlayerStatsParentalScreen viewPlayerStatsParentalScreen;
    private LevelCompleteScreen levelCompleteScreen;
    private CongratulationsScreen congratulationsScreen;

    /** Card layout key for MainMenuScreen. */
    public static final String MAIN_MENU = "MAIN_MENU";
    /** Card layout key for LoginScreen. */
    public static final String LOGIN = "LOGIN";
    /** Card layout key for PlayerScreen. */
    public static final String PLAYER = "PLAYER";
    /** Card layout key for GameModeSelectScreen. */
    public static final String MODE_SELECT = "MODE_SELECT";
    /** Card layout key for LevelSelectScreen. */
    public static final String LEVEL_SELECT = "LEVEL_SELECT";
    /** Card layout key for GameScreen. */
    public static final String GAME = "GAME";
    /** Card layout key for GameOverScreen. */
    public static final String GAME_OVER = "GAME_OVER";
    /** Card layout key for HighScoreScreen. */
    public static final String HIGH_SCORES = "HIGH_SCORES";
    /** Card layout key for TutorialScreen. */
    public static final String TUTORIAL = "TUTORIAL";
    /** Card layout key for PlayerStatsScreen. */
    public static final String PLAYER_STATS = "PLAYER_STATS";
    /** Card layout key for ParentalPinScreen. */
    public static final String PARENTAL_PIN = "PARENTAL_PIN";
    /** Card layout key for ParentalControlScreen. */
    public static final String PARENTAL_CONTROL = "PARENTAL_CONTROL";
    /** Card layout key for ResetMenuScreen. */
    public static final String RESET_MENU = "RESET_MENU";
    /** Card layout key for CreateChildAccountScreen. */
    public static final String CREATE_CHILD_ACCOUNT = "CREATE_CHILD_ACCOUNT";
    /** Card layout key for ResetPasswordParentalScreen. */
    public static final String RESET_PASSWORD_PARENTAL = "RESET_PASSWORD_PARENTAL";
    /** Card layout key for ResetHighScoresScreen. */
    public static final String RESET_HIGH_SCORES = "RESET_HIGH_SCORES";
    /** Card layout key for ResetStatsScreen. */
    public static final String RESET_STATS = "RESET_STATS";
    /** Card layout key for ViewPlayerStatsParentalScreen. */
    public static final String VIEW_PLAYER_STATS_PARENTAL = "VIEW_PLAYER_STATS_PARENTAL";
    /** Card layout key for LevelCompleteScreen. */
    public static final String LEVEL_COMPLETE = "LEVEL_COMPLETE";
    /** Card layout key for CongratulationsScreen. */
    public static final String CONGRATULATIONS = "CONGRATULATIONS";

    /**
     * Constructs the ScreenManager, instantiates every screen, and registers them with the CardLayout.
     * Starts on MAIN_MENU and plays the title music. Called once by KeyHunterApp.
     *
     * @param frame                  the application JFrame
     * @param accountManager         the shared AccountManager instance
     * @param gameEngine             the shared GameEngine instance
     * @param highScoreTable         the shared HighScoreTable instance
     * @param parentalControlService the shared ParentalControlService instance
     * @param persistence            the shared PersistenceService instance
     * @param soundManager           the shared SoundManager instance
     */
    public ScreenManager(JFrame frame, AccountManager accountManager, GameEngine gameEngine,
                         HighScoreTable highScoreTable, ParentalControlService parentalControlService,
                         PersistenceService persistence, SoundManager soundManager) {
        this.frame = frame;
        this.accountManager = accountManager;
        this.gameEngine = gameEngine;
        this.highScoreTable = highScoreTable;
        this.parentalControlService = parentalControlService;
        this.persistence = persistence;
        this.soundManager = soundManager;

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        mainMenuScreen = new MainMenuScreen(this);
        loginScreen = new LoginScreen(this);
        playerScreen = new PlayerScreen(this);
        gameModeSelectScreen = new GameModeSelectScreen(this);
        levelSelectScreen = new LevelSelectScreen(this);
        gameScreen = new GameScreen(this);
        gameOverScreen = new GameOverScreen(this);
        highScoreScreen = new HighScoreScreen(this);
        tutorialScreen = new TutorialScreen(this);
        playerStatsScreen = new PlayerStatsScreen(this);
        parentalPinScreen = new ParentalPinScreen(this);
        parentalControlScreen = new ParentalControlScreen(this);
        resetMenuScreen = new ResetMenuScreen(this);
        createChildAccountScreen = new CreateChildAccountScreen(this);
        resetPasswordParentalScreen = new ResetPasswordParentalScreen(this);
        resetHighScoresScreen = new ResetHighScoresScreen(this);
        resetStatsScreen = new ResetStatsScreen(this);
        viewPlayerStatsParentalScreen = new ViewPlayerStatsParentalScreen(this);
        levelCompleteScreen = new LevelCompleteScreen(this);
        congratulationsScreen = new CongratulationsScreen(this);

        add(mainMenuScreen, MAIN_MENU);
        add(loginScreen, LOGIN);
        add(playerScreen, PLAYER);
        add(gameModeSelectScreen, MODE_SELECT);
        add(levelSelectScreen, LEVEL_SELECT);
        add(gameScreen, GAME);
        add(gameOverScreen, GAME_OVER);
        add(highScoreScreen, HIGH_SCORES);
        add(tutorialScreen, TUTORIAL);
        add(playerStatsScreen, PLAYER_STATS);
        add(parentalPinScreen, PARENTAL_PIN);
        add(parentalControlScreen, PARENTAL_CONTROL);
        add(resetMenuScreen, RESET_MENU);
        add(createChildAccountScreen, CREATE_CHILD_ACCOUNT);
        add(resetPasswordParentalScreen, RESET_PASSWORD_PARENTAL);
        add(resetHighScoresScreen, RESET_HIGH_SCORES);
        add(resetStatsScreen, RESET_STATS);
        add(viewPlayerStatsParentalScreen, VIEW_PLAYER_STATS_PARENTAL);
        add(levelCompleteScreen, LEVEL_COMPLETE);
        add(congratulationsScreen, CONGRATULATIONS);

        showScreen(MAIN_MENU);
        soundManager.playMusic("01. Title BGM.wav");
    }

    /**
     * Transitions to the named screen and calls onScreenShown() if it implements {@link Refreshable}.
     * The primary navigation method called by every screen. Restarts title music when returning to menu screens.
     *
     * @param screenName one of the screen-name constants defined in this class
     */
    public void showScreen(String screenName) {
        cardLayout.show(this, screenName);

        Component[] comps = getComponents();
        for (Component c : comps) {
            if (c.isVisible() && c instanceof Refreshable) {
                ((Refreshable) c).onScreenShown();
            }
        }

        if (GAME.equals(screenName)) {
            SwingUtilities.invokeLater(() -> gameScreen.requestFocusInWindow());
        }

        // Resume menu music when returning to menu screens from the game
        if (PLAYER.equals(screenName) || MAIN_MENU.equals(screenName)) {
            if (!soundManager.isMusicEnabled()) return;
            soundManager.playMusic("01. Title BGM.wav");
        }
    }

    /**
     * Starts a new game session and navigates to GameScreen.
     * Delegates session setup to {@link GameEngine#startNewSession}. Called by LevelSelectScreen.
     *
     * @param modeType      the selected ModeType
     * @param startingLevel the level at which the session begins (1-based)
     */
    public void startGame(ModeType modeType, int startingLevel) {
        gameEngine.startNewSession(accountManager.getCurrentPlayer(), modeType, startingLevel);
        gameScreen.startGameLoop();
        showScreen(GAME);
    }

    /**
     * Passes the RunResult to GameOverScreen and navigates to it. Called by GameScreen on session end.
     *
     * @param result the RunResult for the completed session
     */
    public void showGameOver(RunResult result) {
        gameOverScreen.setResult(result);
        showScreen(GAME_OVER);
    }

    /**
     * Passes the RunResult to LevelCompleteScreen and navigates to it. Called by GameScreen on level clear.
     *
     * @param result the RunResult for the completed level
     */
    public void showLevelComplete(RunResult result) {
        levelCompleteScreen.setResult(result);
        showScreen(LEVEL_COMPLETE);
    }

    /**
     * Passes the RunResult to CongratulationsScreen and navigates to it. Called by GameScreen on game completion.
     *
     * @param result the RunResult for the winning session
     */
    public void showCongratulations(RunResult result) {
        congratulationsScreen.setResult(result);
        showScreen(CONGRATULATIONS);
    }

    /**
     * Saves player data, logs out, and returns to the main menu.
     * Called by the logout button on PlayerScreen and other player-facing screens.
     */
    public void logout() {
        accountManager.saveAll();
        accountManager.logout();
        soundManager.stopMusic();
        showScreen(MAIN_MENU);
        soundManager.playMusic("01. Title BGM.wav");
    }

    /**
     * AccountManager getter. Used by screens that need to read or modify player profiles.
     *
     * @return the shared AccountManager
     */
    public AccountManager getAccountManager() { return accountManager; }

    /**
     * GameEngine getter. Used by GameScreen to drive the active game session.
     *
     * @return the shared GameEngine
     */
    public GameEngine getGameEngine() { return gameEngine; }

    /**
     * HighScoreTable getter. Used by HighScoreScreen and score-related parental screens.
     *
     * @return the shared HighScoreTable
     */
    public HighScoreTable getHighScoreTable() { return highScoreTable; }

    /**
     * ParentalControlService getter. Used by parental screens to verify PINs and manage accounts.
     *
     * @return the shared ParentalControlService
     */
    public ParentalControlService getParentalControlService() { return parentalControlService; }

    /**
     * PersistenceService getter. Used by screens that save or load data directly.
     *
     * @return the shared PersistenceService
     */
    public PersistenceService getPersistence() { return persistence; }

    /**
     * SoundManager getter. Used by screens that trigger sound effects or adjust audio settings.
     *
     * @return the shared SoundManager
     */
    public SoundManager getSoundManager() { return soundManager; }
}
