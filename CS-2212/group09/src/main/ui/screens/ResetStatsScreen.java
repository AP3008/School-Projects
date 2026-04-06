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
 * Parental screen for resetting gameplay statistics for any individual player.
 * Shows a scrollable player list; clicking a player prompts confirmation then calls ParentalControlService.
 * Opened from {@link ResetMenuScreen}. Registered in {@link main.ui.ScreenManager} under RESET_STATS.
 *
 * @author Imad Tahir, Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ResetStatsScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private JPanel listPanel;
    private PixelLabel statusLabel;
    private MenuKeyHandler keyHandler;

    /**
     * Constructs the reset stats screen with a scrollable player list and back button.
     * Player list populated lazily via onScreenShown(). Back and Escape return to {@link ResetMenuScreen}.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public ResetStatsScreen(ScreenManager screenManager) {
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

        PixelLabel titleLabel = new PixelLabel("Reset Stats", 48f, new Color(255, 215, 0));
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

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.RESET_MENU));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(15));
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
     * Rebuilds the player list from AccountManager.
     * Each button prompts confirmation and calls ParentalControlService to reset stats on confirm.
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
                    "Reset all stats for " + username + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    screenManager.getParentalControlService().resetPlayerStats(username);
                    statusLabel.setForeground(new Color(0, 220, 0));
                    statusLabel.setText("Stats reset for: " + username);
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
