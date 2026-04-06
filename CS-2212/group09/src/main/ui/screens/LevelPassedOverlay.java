package main.ui.screens;

import main.ui.ScreenManager;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.StyledButton;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Overlay shown inside GameScreen when the player completes a Normal-mode level.
 * Provides Next Level, Save, and Quit buttons, and is shown and hidden by GameScreen.
 *
 * @author Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class LevelPassedOverlay extends JPanel {

    private MenuKeyHandler keyHandler;

    /**
     * Constructs a LevelPassedOverlay bound to the given GameScreen.
     * Lays out the title and action buttons and installs keyboard navigation.
     *
     * @param gameScreen the parent GameScreen that owns and displays this overlay
     */
    public LevelPassedOverlay(GameScreen gameScreen) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ScreenManager sm = gameScreen.getScreenManager();

        PixelLabel titleLabel = new PixelLabel("Level Passed!", 48f, new Color(0, 220, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton nextLevelBtn = new StyledButton("Next Level", 200, 50);
        nextLevelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextLevelBtn.setMaximumSize(new Dimension(200, 50));

        StyledButton saveBtn = new StyledButton("Save", 200, 50);
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(200, 50));

        StyledButton quitBtn = new StyledButton("Quit", 200, 50);
        quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitBtn.setMaximumSize(new Dimension(200, 50));

        add(Box.createVerticalStrut(10));
        add(titleLabel);
        add(Box.createVerticalStrut(15));
        add(nextLevelBtn);
        add(Box.createVerticalStrut(8));
        add(saveBtn);
        add(Box.createVerticalStrut(8));
        add(quitBtn);

        nextLevelBtn.addActionListener(e -> gameScreen.resumeFromLevelPassed());

        saveBtn.addActionListener(e -> sm.getAccountManager().saveAll());

        quitBtn.addActionListener(e -> {
            sm.getAccountManager().saveAll();
            gameScreen.exitToMenu();
        });

        keyHandler = new MenuKeyHandler(Arrays.asList(nextLevelBtn, saveBtn, quitBtn));
        keyHandler.install(this);
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
