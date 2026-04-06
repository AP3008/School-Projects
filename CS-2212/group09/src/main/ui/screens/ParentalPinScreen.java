package main.ui.screens;

import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.StyledButton;
import main.ui.components.StyledPasswordField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * PIN entry screen guarding access to the parental controls area.
 * Verifies the submitted PIN via ParentalControlService. On success navigates to
 * {@link ParentalControlScreen}; on failure shows an inline error.
 * Shown by {@link main.ui.ScreenManager} from {@link MainMenuScreen}.
 *
 * @author Adam Porbanderwalla, Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ParentalPinScreen extends BackgroundPanel implements Refreshable {

    private MenuKeyHandler keyHandler;
    private StyledPasswordField pinField;
    private PixelLabel errorLabel;

    /**
     * Constructs the PIN entry screen with a password field, submit button, and back button.
     * Submit action verifies the PIN via ParentalControlService. Escape and back return to {@link MainMenuScreen}.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public ParentalPinScreen(ScreenManager screenManager) {
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

        PixelLabel titleLabel = new PixelLabel("Parental Controls", 42f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        PixelLabel pinLabel = new PixelLabel("Enter PIN", 24f);
        pinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        pinField = new StyledPasswordField(10);
        pinField.setMaximumSize(new Dimension(250, 45));
        pinField.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel = new PixelLabel(" ", 18f, new Color(255, 80, 80));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton submitBtn = new StyledButton("Submit");
        submitBtn.setMaximumSize(new Dimension(250, 55));
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(pinLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(pinField);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(errorLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(submitBtn);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(backBtn);

        add(contentPanel);

        ActionListener submitAction = e -> {
            String pin = new String(pinField.getPassword()).trim();
            if (screenManager.getParentalControlService().verifyPin(pin)) {
                pinField.setText("");
                errorLabel.setText(" ");
                screenManager.showScreen(ScreenManager.PARENTAL_CONTROL);
            } else {
                errorLabel.setText("Incorrect PIN");
                pinField.setText("");
            }
        };

        submitBtn.addActionListener(submitAction);
        pinField.addActionListener(submitAction);

        backBtn.addActionListener(e -> {
            pinField.setText("");
            errorLabel.setText(" ");
            screenManager.showScreen(ScreenManager.MAIN_MENU);
        });

        keyHandler = new MenuKeyHandler(Arrays.asList(pinField, submitBtn, backBtn));
        keyHandler.install(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                pinField.setText("");
                errorLabel.setText(" ");
                screenManager.showScreen(ScreenManager.MAIN_MENU);
            }
        });
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Clears the PIN field, resets the error message, and resets keyboard focus.
     */
    @Override
    public void onScreenShown() {
        pinField.setText("");
        errorLabel.setText(" ");
        keyHandler.resetFocus();
    }
}
