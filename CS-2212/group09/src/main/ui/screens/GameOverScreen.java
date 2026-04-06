package main.ui.screens;

import main.engine.RunResult;
import main.modes.ModeType;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.StyledButton;

import javax.swing.*;
import java.awt.*;

/**
 * Full-screen result panel shown after a Normal or Endless mode session ends due to the player
 * losing all lives. Navigated to by ScreenManager.showGameOver and populated via setResult.
 *
 * @author Adam Porbanderwalla, Garv Sharma, Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class GameOverScreen extends BackgroundPanel {

    private ScreenManager screenManager;
    private PixelLabel scoreLabel;
    private PixelLabel wpmLabel;
    private PixelLabel accuracyLabel;
    private PixelLabel errorsLabel;
    private PixelLabel durationLabel;
    private PixelLabel levelLabel;
    private StyledButton playAgainBtn;
    private StyledButton menuBtn;
    private RunResult result;
    private MenuKeyHandler keyHandler;

    /**
     * Constructs a GameOverScreen with a static layout.
     * Builds the title, statistic labels, and Play Again and Back to Menu buttons.
     * Labels are populated later via setResult.
     *
     * @param screenManager the application ScreenManager used to restart a game or navigate to another screen
     */
    public GameOverScreen(ScreenManager screenManager) {
        super("bg/generic-menu-bg.png");
        this.screenManager = screenManager;
        setLayout(new GridBagLayout());

        JPanel contentPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(0, 0, 0, 180));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(25, 50, 25, 50)
        ));
        contentPanel.setOpaque(false);

        PixelLabel titleLabel = new PixelLabel("GAME OVER", 52f, new Color(255, 60, 60));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new PixelLabel("Score: 0", 30f, new Color(255, 215, 0));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        wpmLabel = new PixelLabel("WPM: 0", 24f);
        wpmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        accuracyLabel = new PixelLabel("Accuracy: 0%", 24f);
        accuracyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorsLabel = new PixelLabel("Errors: 0", 24f);
        errorsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        durationLabel = new PixelLabel("Duration: 0s", 24f);
        durationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        levelLabel = new PixelLabel("Level: 1", 24f);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        playAgainBtn = new StyledButton("Play Again");
        playAgainBtn.setMaximumSize(new Dimension(250, 55));
        playAgainBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuBtn = new StyledButton("Back to Menu");
        menuBtn.setMaximumSize(new Dimension(250, 55));
        menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(scoreLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(wpmLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(accuracyLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(errorsLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(durationLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(levelLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(playAgainBtn);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(menuBtn);

        add(contentPanel);

        playAgainBtn.addActionListener(e -> {
            if (result != null) {
                ModeType mode = result.getMode();
                int level = (mode == ModeType.NORMAL) ? result.getLevelReached() : 1;
                screenManager.startGame(mode, level);
            }
        });

        menuBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.PLAYER));
    }

    /**
     * Populates all statistic labels with data from the completed session and rebuilds keyboard navigation.
     * Called by ScreenManager.showGameOver immediately before making this screen visible.
     *
     * @param result the RunResult produced by GameEngine.endSession containing score, WPM, accuracy, errors, duration, and level
     */
    public void setResult(RunResult result) {
        this.result = result;
        scoreLabel.setText("Score: " + result.getScore());
        wpmLabel.setText(String.format("WPM: %.1f", result.getWpm()));
        accuracyLabel.setText(String.format("Accuracy: %.1f%%", result.getAccuracyPercent()));
        errorsLabel.setText("Errors: " + result.getErrors());

        int seconds = result.getDurationInSeconds();
        durationLabel.setText(String.format("Duration: %ds", seconds));
        levelLabel.setText("Level: " + result.getLevelReached());

        // Rebuild key handler for visible buttons
        java.util.List<JComponent> navs = new java.util.ArrayList<>();
        navs.add(playAgainBtn);
        navs.add(menuBtn);
        if (keyHandler != null) keyHandler.uninstall();
        keyHandler = new MenuKeyHandler(navs);
        keyHandler.install(this);
    }
}
