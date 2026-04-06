package main.ui.screens;

import main.account.PlayerProfile;
import main.ui.ScreenManager;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.StyledButton;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Semi-transparent overlay shown inside GameScreen when the player pauses with ESC.
 * Contains Resume, Save, and Quit buttons plus an embedded SettingsPanel, and is managed by GameScreen.
 *
 * @author Adam Porbanderwalla
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class PauseOverlay extends JPanel {

    private MenuKeyHandler keyHandler;
    private SettingsPanel settingsPanel;
    private ScreenManager sm;

    /**
     * Constructs a PauseOverlay bound to the given GameScreen.
     * Lays out the title, action buttons, and embedded SettingsPanel, and installs keyboard navigation.
     *
     * @param gameScreen the parent GameScreen that owns and displays this overlay
     */
    public PauseOverlay(GameScreen gameScreen) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        sm = gameScreen.getScreenManager();

        PixelLabel pauseLabel = new PixelLabel("PAUSED", 48f, new Color(255, 215, 0));
        pauseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton resumeBtn = new StyledButton("Resume", 200, 50);
        resumeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeBtn.setMaximumSize(new Dimension(200, 50));

        StyledButton saveBtn = new StyledButton("Save", 200, 50);
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(200, 50));

        StyledButton quitBtn = new StyledButton("Quit", 200, 50);
        quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitBtn.setMaximumSize(new Dimension(200, 50));

        PlayerProfile player = sm.getAccountManager().getCurrentPlayer();
        settingsPanel = new SettingsPanel(sm.getSoundManager(), player,
                () -> sm.getAccountManager().saveAll());
        settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.setMaximumSize(new Dimension(250, 180));

        add(Box.createVerticalStrut(10));
        add(pauseLabel);
        add(Box.createVerticalStrut(15));
        add(resumeBtn);
        add(Box.createVerticalStrut(8));
        add(saveBtn);
        add(Box.createVerticalStrut(8));
        add(quitBtn);
        add(Box.createVerticalStrut(15));
        add(settingsPanel);

        resumeBtn.addActionListener(e -> gameScreen.resumeGame());
        saveBtn.addActionListener(e -> sm.getGameEngine().saveProgress());
        quitBtn.addActionListener(e -> gameScreen.exitToMenu());

        keyHandler = new MenuKeyHandler(Arrays.asList(resumeBtn, saveBtn, quitBtn));
        keyHandler.install(this);
    }

    /**
     * Synchronises the embedded SettingsPanel with the currently logged-in player.
     * Called by GameScreen.togglePause just before making this overlay visible.
     */
    public void refresh() {
        settingsPanel.refresh(sm.getAccountManager().getCurrentPlayer());
    }

    /**
     * Paints the semi-transparent rounded dark background and grey border of the overlay.
     * Called by the Swing paint cycle before child components are rendered.
     *
     * @param g the Graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
        super.paintComponent(g);
    }
}
