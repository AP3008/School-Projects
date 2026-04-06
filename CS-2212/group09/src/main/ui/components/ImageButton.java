package main.ui.components;

import main.ui.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * JButton that renders itself using PNG images instead of the default Swing look-and-feel.
 * Supports a normal-state and hover-state image, plus a gold focus ring for keyboard navigation.
 * Preferred size is 250x55 pixels. Used in MainMenuScreen and PlayerScreen for primary navigation buttons.
 *
 * @author Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ImageButton extends JButton {

    private BufferedImage normalImg;
    private BufferedImage hoverImg;
    private boolean hovered = false;

    /**
     * Constructs an ImageButton by loading normal and hover images from the ui/ assets folder.
     * Loads ui/baseName.png for the normal state and ui/baseName-hover.png for the hover state.
     *
     * @param baseName the base filename without path or extension (e.g. "btn-play")
     */
    public ImageButton(String baseName) {
        super("");
        normalImg = AssetLoader.loadImage("ui/" + baseName + ".png");
        hoverImg = AssetLoader.loadImage("ui/" + baseName + "-hover.png");
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(250, 55));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
        });
    }

    /**
     * Paints the button image and an optional gold focus ring. Uses hoverImg when hovered or focused,
     * normalImg otherwise. Called by Swing during repaint.
     *
     * @param g the Graphics context supplied by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        BufferedImage img = (hovered || isFocusOwner()) ? hoverImg : normalImg;
        if (img == null) img = normalImg;
        if (img != null) {
            g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        }
        if (isFocusOwner()) {
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
        }
    }
}
