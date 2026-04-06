package main.ui.screens;

import main.account.PlayerProfile;
import main.account.PlayerStats;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.StyledButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parental stats viewer showing a scrollable player list and a per-player stats section.
 * Clicking a player loads their stats from ParentalControlService.
 * Implements {@link main.ui.Refreshable} to rebuild the player list on each visit.
 * Opened from {@link ParentalControlScreen}. Registered in {@link main.ui.ScreenManager} under VIEW_PLAYER_STATS_PARENTAL.
 *
 * @author Jaideep Singh, Rahul
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ViewPlayerStatsParentalScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private JPanel listPanel;
    private JPanel statsPanel;
    private MenuKeyHandler keyHandler;

    /**
     * Constructs the viewer with a scrollable player list, stats panel, and back button.
     * Player list is populated lazily via onScreenShown(). Back and Escape navigate to {@link ParentalControlScreen}.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public ViewPlayerStatsParentalScreen(ScreenManager screenManager) {
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
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        contentPanel.setOpaque(false);

        PixelLabel titleLabel = new PixelLabel("Player Stats", 48f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setMaximumSize(new Dimension(400, 200));
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.PARENTAL_CONTROL));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(backBtn);

        add(contentPanel);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                screenManager.showScreen(ScreenManager.PARENTAL_CONTROL);
            }
        });
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Clears the stats panel and rebuilds the player list from the latest account data.
     */
    @Override
    public void onScreenShown() {
        statsPanel.removeAll();
        statsPanel.revalidate();
        statsPanel.repaint();
        rebuildPlayerList();
    }

    /**
     * Rebuilds the scrollable player list from AccountManager.
     * Each button loads stats for the selected player via showStatsFor().
     * Reinstalls the MenuKeyHandler after rebuilding.
     */
    private void rebuildPlayerList() {
        listPanel.removeAll();
        List<JComponent> navItems = new ArrayList<>();

        Map<String, PlayerProfile> accounts = screenManager.getAccountManager().getAllAccounts();
        for (String username : accounts.keySet()) {
            StyledButton playerBtn = new StyledButton(username, 350, 45);
            playerBtn.setMaximumSize(new Dimension(350, 45));
            playerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerBtn.addActionListener(e -> showStatsFor(username));
            listPanel.add(playerBtn);
            listPanel.add(Box.createVerticalStrut(5));
            navItems.add(playerBtn);
        }

        listPanel.revalidate();
        listPanel.repaint();

        if (keyHandler != null) keyHandler.uninstall();
        keyHandler = new MenuKeyHandler(navItems);
        keyHandler.install(this);
    }

    /**
     * Populates the stats panel with statistics for the given player via ParentalControlService.
     * Shows an error label if the player is not found.
     *
     * @param username the username to look up; must correspond to an existing account
     */
    private void showStatsFor(String username) {
        statsPanel.removeAll();

        PlayerStats stats = screenManager.getParentalControlService().viewPlayerStats(username);
        if (stats == null) {
            PixelLabel notFound = new PixelLabel("Player not found", 18f, new Color(255, 80, 80));
            notFound.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPanel.add(notFound);
        } else {
            addStatLine("Player: " + username, 22f, new Color(255, 215, 0));
            addStatLine(String.format("Average WPM: %.1f", stats.getAverageWPM()), 18f, Color.WHITE);
            addStatLine(String.format("Peak WPM: %.1f", stats.getPeakWPM()), 18f, Color.WHITE);
            addStatLine(String.format("Accuracy: %.1f%%", stats.getOverallAccuracy()), 18f, Color.WHITE);
            addStatLine("Words Correct: " + stats.getTotalWordsCorrect(), 18f, Color.WHITE);
            addStatLine("Sessions: " + stats.getTotalSessions(), 18f, Color.WHITE);
            addStatLine("Total Errors: " + stats.getTotalErrors(), 18f, Color.WHITE);
            long totalMs = stats.getTotalTimePlayed();
            long totalSec = totalMs / 1000;
            long hours = totalSec / 3600;
            long mins = (totalSec % 3600) / 60;
            long secs = totalSec % 60;
            addStatLine(String.format("Time Played: %dh %dm %ds", hours, mins, secs), 18f, Color.WHITE);
            addStatLine("High Score: " + stats.getHighScore(), 18f, Color.WHITE);
            addStatLine("Normal High Score: " + stats.getHighScoreNormal(), 18f, Color.WHITE);
            addStatLine("Timed High Score: " + stats.getHighScoreTimed(), 18f, Color.WHITE);
            addStatLine("Endless High Score: " + stats.getHighScoreEndless(), 18f, Color.WHITE);
            addStatLine("Highest Level: " + stats.getHighestLevel(), 18f, Color.WHITE);
        }

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    /**
     * Appends a PixelLabel with the given text, size, and colour to the stats panel.
     *
     * @param text  the stat text to display
     * @param size  font size in points
     * @param color foreground colour for the label
     */
    private void addStatLine(String text, float size, Color color) {
        PixelLabel label = new PixelLabel(text, size, color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(label);
        statsPanel.add(Box.createVerticalStrut(4));
    }
}
