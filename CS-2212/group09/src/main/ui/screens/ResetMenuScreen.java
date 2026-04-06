package main.ui.screens;

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
 * Parental controls sub-menu grouping three reset operations:
 * Reset Password, Reset Stats, and Reset High Scores.
 * Opened from {@link ParentalControlScreen}. Registered in {@link main.ui.ScreenManager} under RESET_MENU.
 *
 * @author Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ResetMenuScreen extends BackgroundPanel implements Refreshable {

    private MenuKeyHandler keyHandler;

    /**
     * Constructs the reset menu with navigation buttons and keyboard navigation.
     * Escape key returns to {@link ParentalControlScreen}.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public ResetMenuScreen(ScreenManager screenManager) {
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
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        contentPanel.setOpaque(false);

        PixelLabel titleLabel = new PixelLabel("Reset Menu", 48f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton resetPasswordBtn = new ImageButton("reset-password-button");
        resetPasswordBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetPasswordBtn.setMaximumSize(new Dimension(300, 65));

        ImageButton resetStatsBtn = new ImageButton("reset-stats-button");
        resetStatsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetStatsBtn.setMaximumSize(new Dimension(300, 65));

        ImageButton resetHighScoresBtn = new ImageButton("reset-highscores-button");
        resetHighScoresBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetHighScoresBtn.setMaximumSize(new Dimension(300, 65));

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(250, 55));

        resetPasswordBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.RESET_PASSWORD_PARENTAL));
        resetStatsBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.RESET_STATS));
        resetHighScoresBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.RESET_HIGH_SCORES));
        backBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.PARENTAL_CONTROL));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(resetPasswordBtn);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(resetStatsBtn);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(resetHighScoresBtn);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(backBtn);

        add(contentPanel);

        keyHandler = new MenuKeyHandler(Arrays.asList(resetPasswordBtn, resetStatsBtn, resetHighScoresBtn, backBtn));
        keyHandler.install(this);

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
     * Called by {@link ScreenManager} when this screen becomes visible. Resets keyboard focus.
     */
    @Override
    public void onScreenShown() {
        keyHandler.resetFocus();
    }
}
