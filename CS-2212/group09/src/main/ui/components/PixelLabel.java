package main.ui.components;

import main.ui.AssetLoader;

import javax.swing.*;
import java.awt.*;

/**
 * JLabel that renders text using the Jersey10-Regular pixel font loaded via {@link AssetLoader#getPixelFont(float)}.
 * All instances are horizontally centred. Used throughout every screen for titles, headers, and display text.
 *
 * @author Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class PixelLabel extends JLabel {

    /**
     * Constructs a PixelLabel with white foreground text at the specified font size.
     *
     * @param text     the text to display
     * @param fontSize point size for the pixel font
     */
    public PixelLabel(String text, float fontSize) {
        super(text);
        setFont(AssetLoader.getPixelFont(fontSize));
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Constructs a PixelLabel with an explicit foreground colour at the specified font size.
     *
     * @param text     the text to display
     * @param fontSize point size for the pixel font
     * @param color    the foreground colour
     */
    public PixelLabel(String text, float fontSize, Color color) {
        super(text);
        setFont(AssetLoader.getPixelFont(fontSize));
        setForeground(color);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

}
