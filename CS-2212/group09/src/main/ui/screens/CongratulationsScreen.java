package main.ui.screens;

import main.engine.RunResult;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.StyledButton;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Full-screen congratulations panel shown after the player completes all 10 Normal-mode levels.
 * Navigated to by ScreenManager.showCongratulations and populated via setResult.
 *
 * @author Adam Porbanderwalla, Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class CongratulationsScreen extends BackgroundPanel {

    private PixelLabel scoreLabel;

    private PixelLabel wpmLabel;

    private PixelLabel accuracyLabel;

    private PixelLabel errorsLabel;

    private PixelLabel durationLabel;

    private PixelLabel levelLabel;

    private MenuKeyHandler keyHandler;

    /**
     * Constructs a CongratulationsScreen with a static layout.
     * Builds the title, statistic labels, and Quit button, which returns to the player screen.
     * Labels are populated later via setResult.
     *
     * @param screenManager the application ScreenManager used to navigate back to the player selection screen
     */
    public CongratulationsScreen(ScreenManager screenManager) {
        super("bg/generic-menu-bg.png");
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

        PixelLabel titleLabel = new PixelLabel("CONGRATULATIONS!", 52f, new Color(255, 215, 0));
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

        levelLabel = new PixelLabel("Level: 10", 24f);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton quitBtn = new StyledButton("Quit");
        quitBtn.setMaximumSize(new Dimension(250, 55));
        quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        contentPanel.add(quitBtn);

        add(contentPanel);

        quitBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.PLAYER));

        keyHandler = new MenuKeyHandler(Arrays.asList(quitBtn));
        keyHandler.install(this);
    }

    /**
     * Populates all statistic labels with data from the completed Normal-mode run.
     * Called by ScreenManager.showCongratulations immediately before making this screen visible.
     *
     * @param result the RunResult produced by GameEngine.endSession after level 10 is completed
     */
    public void setResult(RunResult result) {
        scoreLabel.setText("Score: " + result.getScore());
        wpmLabel.setText(String.format("WPM: %.1f", result.getWpm()));
        accuracyLabel.setText(String.format("Accuracy: %.1f%%", result.getAccuracyPercent()));
        errorsLabel.setText("Errors: " + result.getErrors());
        int seconds = result.getDurationInSeconds();
        durationLabel.setText(String.format("Duration: %ds", seconds));
        levelLabel.setText("Level: " + result.getLevelReached());
    }
}
