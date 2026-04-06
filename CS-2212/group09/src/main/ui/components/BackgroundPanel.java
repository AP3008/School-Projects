package main.ui.components;

import main.ui.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * JPanel that renders a full-bleed background image scaled to fill the panel at paint time.
 * Used as the root container in virtually every screen including MainMenuScreen, PlayerScreen,
 * LoginScreen, GameScreen, and GameOverScreen.
 *
 * @author Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class BackgroundPanel extends JPanel {

    private BufferedImage backgroundImage;

    /**
     * Constructs a BackgroundPanel and loads the image at the given path via {@link AssetLoader#loadImage(String)}.
     *
     * @param imagePath path relative to src/main/assets/
     */
    public BackgroundPanel(String imagePath) {
        this.backgroundImage = AssetLoader.loadImage(imagePath);
        setOpaque(false);
    }

    /**
     * Paints the background image scaled to the panel's current size with bilinear interpolation.
     * Called by Swing during repaint.
     *
     * @param g the Graphics context supplied by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

}
