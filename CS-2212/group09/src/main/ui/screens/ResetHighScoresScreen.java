package main.ui.screens;

import main.account.PlayerProfile;
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
 * Parental screen for resetting high scores per player or all at once.
 * Per-player: confirmation dialog then resets via HighScoreTable and PersistenceService.
 * Reset ALL: clears the entire high-score table and all player stats.
 * Opened from {@link ResetMenuScreen}. Registered in {@link main.ui.ScreenManager} under RESET_HIGH_SCORES.
 *
 * @author Adam Porbanderwalla, Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ResetHighScoresScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private JPanel listPanel;
    private PixelLabel statusLabel;
    private MenuKeyHandler keyHandler;

    /**
     * Constructs the reset high scores screen with a player list, Reset ALL button, and back button.
     * Player list populated lazily via onScreenShown(). Back and Escape return to {@link ResetMenuScreen}.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public ResetHighScoresScreen(ScreenManager screenManager) {
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

        PixelLabel titleLabel = new PixelLabel("Reset High Scores", 48f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new PixelLabel(" ", 16f, new Color(0, 220, 0));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setMaximumSize(new Dimension(400, 350));
        scrollPane.setPreferredSize(new Dimension(400, 350));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton resetAllBtn = new StyledButton("Reset ALL High Scores + Stats", 350, 50);
        resetAllBtn.setMaximumSize(new Dimension(350, 50));
        resetAllBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetAllBtn.addActionListener(e -> resetAll());

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.RESET_MENU));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(resetAllBtn);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(backBtn);

        add(contentPanel);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                screenManager.showScreen(ScreenManager.RESET_MENU);
            }
        });
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Clears the status label and rebuilds the player list.
     */
    @Override
    public void onScreenShown() {
        statusLabel.setText(" ");
        rebuildPlayerList();
    }

    /**
     * Prompts a WARNING-level confirmation, then resets the entire high-score table
     * and all player statistics via ParentalControlService. Updates the status label on completion.
     */
    private void resetAll() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset ALL high scores AND all player stats? This cannot be undone.",
            "Confirm Reset All", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Reset entire high score table
        screenManager.getParentalControlService().resetHighScores();
        screenManager.getPersistence().saveHighScores(screenManager.getHighScoreTable());

        // Wipe every player's stats
        for (String username : screenManager.getAccountManager().getAllAccounts().keySet()) {
            screenManager.getParentalControlService().resetPlayerStats(username);
        }

        statusLabel.setForeground(new Color(0, 220, 0));
        statusLabel.setText("All high scores and stats have been reset.");
    }

    /**
     * Rebuilds the player list from AccountManager.
     * Each button prompts confirmation and resets that player's high scores on confirm.
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
            playerBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Reset high scores for " + username + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    screenManager.getHighScoreTable().resetForPlayer(username);
                    screenManager.getPersistence().saveHighScores(screenManager.getHighScoreTable());
                    statusLabel.setForeground(new Color(0, 220, 0));
                    statusLabel.setText("High scores reset for: " + username);
                }
            });
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
}
