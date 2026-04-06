package main.ui.screens;

import main.account.PlayerProfile;
import main.account.PlayerStats;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Screen displaying a comprehensive stats summary for the logged-in player.
 * Reads all values from PlayerStats on each visit: WPM, accuracy, session counts,
 * time played, high scores per mode, and highest level reached.
 * Back button and Escape return to {@link PlayerScreen}.
 * Implements {@link main.ui.Refreshable} to refresh stats on each visit. Shown by {@link ScreenManager}.
 *
 * @author Adam Porbanderwalla, Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class PlayerStatsScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private PixelLabel usernameLabel;
    private PixelLabel avgWpmLabel;
    private PixelLabel peakWpmLabel;
    private PixelLabel accuracyLabel;
    private PixelLabel wordsLabel;
    private PixelLabel sessionsLabel;
    private PixelLabel errorsLabel;
    private PixelLabel timePlayedLabel;
    private PixelLabel highScoreLabel;
    private PixelLabel highestLevelLabel;
    private PixelLabel highScoreNormalLabel;
    private PixelLabel highScoreTimedLabel;
    private PixelLabel highScoreEndlessLabel;
    private MenuKeyHandler keyHandler;

    /**
     * Constructs the player stats screen with all stat labels initialised to "---"
     * and a Back button. Registers the Escape key to return to {@link PlayerScreen}.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public PlayerStatsScreen(ScreenManager screenManager) {
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

        PixelLabel titleLabel = new PixelLabel("Player Stats", 48f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameLabel = new PixelLabel("Player: ---", 28f);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        avgWpmLabel = new PixelLabel("Average WPM: ---", 24f);
        avgWpmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        peakWpmLabel = new PixelLabel("Peak WPM: ---", 24f);
        peakWpmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        accuracyLabel = new PixelLabel("Accuracy: ---", 24f);
        accuracyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        wordsLabel = new PixelLabel("Words Correct: ---", 24f);
        wordsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sessionsLabel = new PixelLabel("Sessions: ---", 24f);
        sessionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorsLabel = new PixelLabel("Total Errors: ---", 24f);
        errorsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timePlayedLabel = new PixelLabel("Time Played: ---", 24f);
        timePlayedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        highScoreLabel = new PixelLabel("High Score: ---", 24f);
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        highScoreNormalLabel = new PixelLabel("Normal High Score: ---", 24f);
        highScoreNormalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        highScoreTimedLabel = new PixelLabel("Timed High Score: ---", 24f);
        highScoreTimedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        highScoreEndlessLabel = new PixelLabel("Endless High Score: ---", 24f);
        highScoreEndlessLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        highestLevelLabel = new PixelLabel("Highest Level: ---", 24f);
        highestLevelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.PLAYER));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(usernameLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(avgWpmLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(peakWpmLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(accuracyLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(wordsLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(sessionsLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(errorsLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(timePlayedLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(highScoreLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(highScoreNormalLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(highScoreTimedLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(highScoreEndlessLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(highestLevelLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(backBtn);

        add(contentPanel);

        keyHandler = new MenuKeyHandler(Arrays.asList(backBtn));
        keyHandler.install(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                screenManager.showScreen(ScreenManager.PLAYER);
            }
        });
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Populates all stat labels from the current PlayerProfile's PlayerStats.
     * Time played is converted from milliseconds to hours/minutes/seconds. Resets keyboard focus.
     */
    @Override
    public void onScreenShown() {
        PlayerProfile player = screenManager.getAccountManager().getCurrentPlayer();
        if (player != null) {
            PlayerStats stats = player.getStats();
            usernameLabel.setText("Player: " + player.getUsername());
            avgWpmLabel.setText(String.format("Average WPM: %.1f", stats.getAverageWPM()));
            peakWpmLabel.setText(String.format("Peak WPM: %.1f", stats.getPeakWPM()));
            accuracyLabel.setText(String.format("Accuracy: %.1f%%", stats.getOverallAccuracy()));
            wordsLabel.setText("Words Correct: " + stats.getTotalWordsCorrect());
            sessionsLabel.setText("Sessions: " + stats.getTotalSessions());
            errorsLabel.setText("Total Errors: " + stats.getTotalErrors());
            long totalMs = stats.getTotalTimePlayed();
            long totalSec = totalMs / 1000;
            long hours = totalSec / 3600;
            long mins = (totalSec % 3600) / 60;
            long secs = totalSec % 60;
            timePlayedLabel.setText(String.format("Time Played: %dh %dm %ds", hours, mins, secs));
            highScoreLabel.setText("High Score: " + stats.getHighScore());
            highScoreNormalLabel.setText("Normal High Score: " + stats.getHighScoreNormal());
            highScoreTimedLabel.setText("Timed High Score: " + stats.getHighScoreTimed());
            highScoreEndlessLabel.setText("Endless High Score: " + stats.getHighScoreEndless());
            highestLevelLabel.setText("Highest Level: " + stats.getHighestLevel());
        }
        keyHandler.resetFocus();
    }
}
