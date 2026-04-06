package main.ui.screens;

import main.account.AccountManager;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Login screen presenting a username and password form for player authentication.
 * Delegates to AccountManager on submission. On success navigates to {@link PlayerScreen};
 * on failure shows an inline error message. Back button and Escape return to {@link MainMenuScreen}.
 * Keyboard navigation is handled by {@link MenuKeyHandler}. Shown by {@link ScreenManager}.
 *
 *Artificial Intelligence Tool: Claude; Programming: debugging and correcting syntax errors; 
 * @author Adam Porbanderwalla, Garv Sharma, Rahul
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class LoginScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private StyledTextField usernameField;
    private StyledPasswordField passwordField;
    private PixelLabel errorLabel;
    private MenuKeyHandler keyHandler;

    /**
     * Constructs the login screen with username/password fields, login and back buttons,
     * and an error label. Attaches action listeners and registers the Escape key shortcut.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public LoginScreen(ScreenManager screenManager) {
        super("bg/generic-menu-bg.png");
        this.screenManager = screenManager;
        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(0, 0, 0, 180));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        formPanel.setOpaque(false);

        PixelLabel titleLabel = new PixelLabel("Login", 48f);
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

        errorLabel = new PixelLabel(" ", 18f, new Color(255, 80, 80));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton loginBtn = new StyledButton("Login");
        loginBtn.setMaximumSize(new Dimension(250, 55));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(loginBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(backBtn);

        add(formPanel);

        ActionListener loginAction = e -> attemptLogin();
        loginBtn.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);

        backBtn.addActionListener(e -> {
            clearFields();
            screenManager.showScreen(ScreenManager.MAIN_MENU);
        });

        keyHandler = new MenuKeyHandler(Arrays.asList(usernameField, passwordField, loginBtn, backBtn));
        keyHandler.install(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                clearFields();
                screenManager.showScreen(ScreenManager.MAIN_MENU);
            }
        });
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible. Resets keyboard focus.
     */
    @Override
    public void onScreenShown() {
        keyHandler.resetFocus();
    }

    /**
     * Reads the username and password fields and delegates to AccountManager for authentication.
     * On success applies the player's audio settings and navigates to {@link PlayerScreen}.
     * On failure shows an error message and clears the password field.
     */
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }

        AccountManager am = screenManager.getAccountManager();
        boolean success = am.login(username, password);

        if (success) {
            screenManager.getSoundManager().applySettings(am.getCurrentPlayer().getSettings());
            clearFields();
            screenManager.showScreen(ScreenManager.PLAYER);
        } else {
            errorLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }

    /**
     * Resets username, password, and error label to empty. Called before navigating away.
     */
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }
}
