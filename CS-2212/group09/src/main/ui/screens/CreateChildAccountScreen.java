package main.ui.screens;

import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.ImageButton;
import main.ui.components.StyledButton;
import main.ui.components.StyledPasswordField;
import main.ui.components.StyledTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Account creation form allowing a parent to register a new child account.
 * Validates username uniqueness, non-empty fields, and matching passwords before
 * delegating to AccountManager. Shows a confirmation dialog on success.
 * Opened from {@link ParentalControlScreen}. Registered in {@link main.ui.ScreenManager} under CREATE_CHILD_ACCOUNT.
 *
 * @author Adam Porbanderwalla, Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class CreateChildAccountScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private MenuKeyHandler keyHandler;
    private StyledTextField usernameField;
    private StyledPasswordField passwordField;
    private StyledPasswordField confirmPasswordField;
    private PixelLabel statusLabel;

    /**
     * Constructs the account creation screen with username, password, and confirm password fields.
     * Done button and Escape key wire to createAccount() and navigation back to {@link ParentalControlScreen}.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public CreateChildAccountScreen(ScreenManager screenManager) {
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

        PixelLabel titleLabel = new PixelLabel("Create Account", 48f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        PixelLabel userLabel = new PixelLabel("Username", 22f);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new StyledTextField(15);
        usernameField.setMaximumSize(new Dimension(300, 45));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        PixelLabel passLabel = new PixelLabel("Password", 22f);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new StyledPasswordField(15);
        passwordField.setMaximumSize(new Dimension(300, 45));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        PixelLabel confirmLabel = new PixelLabel("Confirm Password", 22f);
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmPasswordField = new StyledPasswordField(15);
        confirmPasswordField.setMaximumSize(new Dimension(300, 45));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new PixelLabel(" ", 18f, new Color(255, 80, 80));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton doneBtn = new StyledButton("Done");
        doneBtn.setMaximumSize(new Dimension(250, 55));
        doneBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(backBtn);
        buttonRow.add(doneBtn);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(userLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(usernameField);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(passLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(confirmLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(confirmPasswordField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(buttonRow);

        add(contentPanel);

        doneBtn.addActionListener(e -> createAccount());

        backBtn.addActionListener(e -> {
            clearFields();
            screenManager.showScreen(ScreenManager.PARENTAL_CONTROL);
        });

        keyHandler = new MenuKeyHandler(Arrays.asList(usernameField, passwordField, confirmPasswordField, doneBtn, backBtn));
        keyHandler.install(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                clearFields();
                screenManager.showScreen(ScreenManager.PARENTAL_CONTROL);
            }
        });
    }

    /**
     * Validates form fields and attempts to create the account via AccountManager.
     * Shows errors for empty fields, mismatched passwords, or duplicate usernames.
     * On success clears the form and shows a confirmation dialog.
     */
    private void createAccount() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setStatus("Please fill in all fields", false);
            return;
        }
        if (!password.equals(confirm)) {
            setStatus("Passwords do not match", false);
            return;
        }

        boolean created = screenManager.getAccountManager().createAccount(username, password);
        if (created) {
            clearFields();
            JOptionPane.showMessageDialog(this,
                "Account \"" + username + "\" has been created successfully!",
                "Account Created",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            setStatus("Username already exists", false);
        }
    }

    /**
     * Updates the inline status label. Green for success, red for error.
     *
     * @param msg     the message to display
     * @param success true for green success colour, false for red error colour
     */
    private void setStatus(String msg, boolean success) {
        statusLabel.setForeground(success ? new Color(0, 220, 0) : new Color(255, 80, 80));
        statusLabel.setText(msg);
    }

    /**
     * Clears all input fields and resets the status label. Called before navigating away and on screen shown.
     */
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        statusLabel.setText(" ");
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Clears all form fields and resets keyboard focus.
     */
    @Override
    public void onScreenShown() {
        clearFields();
        keyHandler.resetFocus();
    }
}
