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
 * Main parental controls hub shown after PIN verification in {@link ParentalPinScreen}.
 * Provides four navigation buttons: Create Child Account, View Player Stats, Reset Menu, and Exit.
 * Registered in {@link main.ui.ScreenManager} under PARENTAL_CONTROL.
 *
 * Artificial Intelligence Tool: Claude; programming: syntax correction;
 * @author Rahul, Jaideep Singh
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ParentalControlScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private MenuKeyHandler keyHandler;

    /**
     * Constructs the parental controls menu with navigation buttons and keyboard navigation.
     * Escape key returns to {@link MainMenuScreen}.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public ParentalControlScreen(ScreenManager screenManager) {
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
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        contentPanel.setOpaque(false);

        PixelLabel titleLabel = new PixelLabel("Parental Controls Menu", 40f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton createChildBtn = new ImageButton("create-child-account-button");
        createChildBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createChildBtn.setMaximumSize(new Dimension(300, 65));

        ImageButton viewStatsBtn = new ImageButton("view-player-stats-button");
        viewStatsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewStatsBtn.setMaximumSize(new Dimension(300, 65));

        ImageButton resetMenuBtn = new ImageButton("reset-menu-button");
        resetMenuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetMenuBtn.setMaximumSize(new Dimension(300, 65));

        ImageButton exitBtn = new ImageButton("exit-button");
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setMaximumSize(new Dimension(300, 65));

        createChildBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.CREATE_CHILD_ACCOUNT));
        viewStatsBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.VIEW_PLAYER_STATS_PARENTAL));
        resetMenuBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.RESET_MENU));
        exitBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.MAIN_MENU));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(createChildBtn);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(viewStatsBtn);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(resetMenuBtn);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(exitBtn);

        add(contentPanel);

        keyHandler = new MenuKeyHandler(Arrays.asList(createChildBtn, viewStatsBtn, resetMenuBtn, exitBtn));
        keyHandler.install(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                screenManager.showScreen(ScreenManager.MAIN_MENU);
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
