package main.ui.components;

import main.ui.AssetLoader;

import javax.swing.*;
import java.awt.*;

/**
 * JPasswordField with the KeyHunter dark-theme styling matching {@link StyledTextField}.
 * Characters are masked with '*'. Preferred size is 300x45 pixels.
 * Used in LoginScreen, CreateChildAccountScreen, and ResetPasswordParentalScreen for password input.
 *
 * @author Adam Porbanderwalla
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class StyledPasswordField extends JPasswordField {

    /**
     * Constructs a StyledPasswordField with the given column hint and applies the dark-theme styling.
     *
     * @param columns column hint forwarded to JPasswordField; the explicit 300x45 preferred size takes precedence
     */
    public StyledPasswordField(int columns) {
        super(columns);
        setFont(AssetLoader.getPixelFont(18f));
        setBackground(new Color(30, 30, 30));
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setEchoChar('*');
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        setPreferredSize(new Dimension(300, 45));
    }
}
