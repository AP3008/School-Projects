package main.ui.components;

import main.ui.AssetLoader;

import javax.swing.*;
import java.awt.*;

/**
 * JTextField with the KeyHunter dark-theme styling. Dark background, white text, grey border,
 * and the pixel font at 18pt. Preferred size is 300x45 pixels.
 * Used in LoginScreen, CreateChildAccountScreen, and ResetPasswordParentalScreen for plain-text input.
 *
 * @author Adam Porbanderwalla
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class StyledTextField extends JTextField {

    /**
     * Constructs a StyledTextField with the given column hint and applies the dark-theme styling.
     *
     * @param columns column hint forwarded to JTextField; the explicit 300x45 preferred size takes precedence
     */
    public StyledTextField(int columns) {
        super(columns);
        setFont(AssetLoader.getPixelFont(18f));
        setBackground(new Color(30, 30, 30));
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        setPreferredSize(new Dimension(300, 45));
    }
}
