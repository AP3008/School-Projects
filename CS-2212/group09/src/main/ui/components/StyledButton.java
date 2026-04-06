package main.ui.components;

import main.ui.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * JButton with a custom dark-theme appearance replacing the default Swing look-and-feel.
 * Supports normal, hover/focused, pressed, and disabled visual states.
 * Default preferred size is 250x55 pixels. Used throughout all non-menu screens including
 * LoginScreen, ParentalControlScreen, GameOverScreen, and CreateChildAccountScreen.
 *
 * @author Garv Sharma, Adam Porbanderwalla
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class StyledButton extends JButton {

    private boolean hovered = false;
    private boolean pressed = false;

    /** Background colour in the normal state. */
    private static final Color BG_NORMAL = new Color(42, 42, 42);

    /** Background colour when hovered or focused. */
    private static final Color BG_HOVER = new Color(58, 58, 58);

    /** Background colour while the button is pressed. */
    private static final Color BG_PRESSED = new Color(26, 26, 26);

    /** Border colour drawn around the button at all times. */
    private static final Color BORDER_COLOR = new Color(80, 80, 80);

    /** Label text colour in the normal state. */
    private static final Color TEXT_NORMAL = Color.WHITE;

    /** Label text colour when hovered or focused. */
    private static final Color TEXT_HOVER = new Color(255, 215, 0);

    /** Focus ring colour drawn when the button owns keyboard focus. */
    private static final Color FOCUS_COLOR = new Color(255, 215, 0);

    /**
     * Constructs a StyledButton with the default 250x55 preferred size.
     *
     * @param text the button label
     */
    public StyledButton(String text) {
        this(text, 250, 55);
    }

    /**
     * Constructs a StyledButton with an explicit preferred size.
     * Loads the pixel font at 24pt via {@link AssetLoader#getPixelFont(float)}.
     *
     * @param text   the button label
     * @param width  preferred width in pixels
     * @param height preferred height in pixels
     */
    public StyledButton(String text, int width, int height) {
        super(text);
        setFont(AssetLoader.getPixelFont(24f));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(width, height));
        setForeground(TEXT_NORMAL);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { hovered = false; pressed = false; repaint(); }
            @Override
            public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
            @Override
            public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
        });
    }

    /**
     * Paints the background, border, optional focus ring, and centred label text.
     * Called by Swing during repaint.
     *
     * @param g the Graphics context supplied by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = pressed ? BG_PRESSED : (hovered || isFocusOwner()) ? BG_HOVER : BG_NORMAL;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);

        if (isFocusOwner()) {
            g2.setColor(FOCUS_COLOR);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
        }

        g2.setFont(getFont());
        Color textColor = (hovered || isFocusOwner()) ? TEXT_HOVER : TEXT_NORMAL;
        if (!isEnabled()) {
            textColor = new Color(100, 100, 100);
        }
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), textX, textY);
    }
}
