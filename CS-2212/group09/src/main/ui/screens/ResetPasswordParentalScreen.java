package main.ui.screens;

import main.account.PlayerProfile;
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
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Two-card screen allowing a parent to reset any player's password.
 * The LIST card shows all accounts; selecting one flips to the FORM card with new/confirm password fields.
 * Escape navigates from FORM back to LIST, and from LIST back to {@link ResetMenuScreen}.
 * Registered in {@link main.ui.ScreenManager} under RESET_PASSWORD_PARENTAL.
 *
 * @author Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class ResetPasswordParentalScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Player list view
    private JPanel listPanel;

    // Password form view
    private MenuKeyHandler keyHandler;
    private String selectedUsername;
    private PixelLabel formTitleLabel;
    private StyledPasswordField newPasswordField;
    private StyledPasswordField confirmPasswordField;
    private PixelLabel statusLabel;

    /**
     * Constructs the reset-password screen with LIST and FORM cards using CardLayout.
     * Player list is populated via rebuildPlayerList(). Done button triggers resetPassword().
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public ResetPasswordParentalScreen(ScreenManager screenManager) {
        super("bg/generic-menu-bg.png");
        this.screenManager = screenManager;
        setLayout(new GridBagLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        // === Player List View ===
        JPanel listView = new JPanel(new GridBagLayout());
        listView.setOpaque(false);

        JPanel listContent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        listContent.setLayout(new BoxLayout(listContent, BoxLayout.Y_AXIS));
        listContent.setBackground(new Color(0, 0, 0, 180));
        listContent.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        listContent.setOpaque(false);

        PixelLabel listTitle = new PixelLabel("Reset Password", 48f, new Color(255, 215, 0));
        listTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        PixelLabel selectLabel = new PixelLabel("Select a player:", 22f, new Color(200, 200, 200));
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setMaximumSize(new Dimension(400, 350));
        scrollPane.setPreferredSize(new Dimension(400, 350));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton listBackBtn = new ImageButton("back-button");
        listBackBtn.setMaximumSize(new Dimension(250, 55));
        listBackBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        listBackBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.RESET_MENU));

        listContent.add(listTitle);
        listContent.add(Box.createVerticalStrut(10));
        listContent.add(selectLabel);
        listContent.add(Box.createVerticalStrut(10));
        listContent.add(scrollPane);
        listContent.add(Box.createVerticalStrut(15));
        listContent.add(listBackBtn);

        listView.add(listContent);

        // === Password Form View ===
        JPanel formView = new JPanel(new GridBagLayout());
        formView.setOpaque(false);

        JPanel formContent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(new Color(0, 0, 0, 180));
        formContent.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        formContent.setOpaque(false);

        formTitleLabel = new PixelLabel("Reset Password", 48f, new Color(255, 215, 0));
        formTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        PixelLabel newPassLabel = new PixelLabel("New Password", 22f);
        newPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        newPasswordField = new StyledPasswordField(15);
        newPasswordField.setMaximumSize(new Dimension(300, 45));
        newPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        PixelLabel confirmLabel = new PixelLabel("Confirm Password", 22f);
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmPasswordField = new StyledPasswordField(15);
        confirmPasswordField.setMaximumSize(new Dimension(300, 45));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new PixelLabel(" ", 18f, new Color(255, 80, 80));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton formBackBtn = new ImageButton("back-button");
        formBackBtn.setMaximumSize(new Dimension(250, 55));
        formBackBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton doneBtn = new StyledButton("Done");
        doneBtn.setMaximumSize(new Dimension(250, 55));
        doneBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(formBackBtn);
        buttonRow.add(doneBtn);

        formContent.add(formTitleLabel);
        formContent.add(Box.createVerticalStrut(20));
        formContent.add(newPassLabel);
        formContent.add(Box.createVerticalStrut(5));
        formContent.add(newPasswordField);
        formContent.add(Box.createVerticalStrut(15));
        formContent.add(confirmLabel);
        formContent.add(Box.createVerticalStrut(5));
        formContent.add(confirmPasswordField);
        formContent.add(Box.createVerticalStrut(10));
        formContent.add(statusLabel);
        formContent.add(Box.createVerticalStrut(15));
        formContent.add(buttonRow);

        formView.add(formContent);

        cardPanel.add(listView, "LIST");
        cardPanel.add(formView, "FORM");

        add(cardPanel);

        doneBtn.addActionListener(e -> resetPassword());
        formBackBtn.addActionListener(e -> cardLayout.show(cardPanel, "LIST"));

        // Escape goes back from form to list, or from list to reset menu
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (formView.isShowing()) {
                    cardLayout.show(cardPanel, "LIST");
                } else {
                    screenManager.showScreen(ScreenManager.RESET_MENU);
                }
            }
        });

        keyHandler = new MenuKeyHandler(Arrays.asList(newPasswordField, confirmPasswordField, doneBtn, formBackBtn));
        keyHandler.install(this);
    }

    /**
     * Validates new/confirm password fields and resets the selected player's password via ParentalControlService.
     * Shows errors for empty fields or mismatched passwords. Shows a green confirmation on success.
     */
    private void resetPassword() {
        String newPass = new String(newPasswordField.getPassword()).trim();
        String confirm = new String(confirmPasswordField.getPassword()).trim();

        if (newPass.isEmpty() || confirm.isEmpty()) {
            setStatus("Please fill in all fields", false);
            return;
        }
        if (!newPass.equals(confirm)) {
            setStatus("Passwords do not match", false);
            return;
        }

        screenManager.getParentalControlService().resetPassword(selectedUsername, newPass);
        setStatus("Password reset for: " + selectedUsername, true);
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    /**
     * Updates the inline status label. Green for success, red for error.
     *
     * @param msg     the message to display
     * @param success true for green, false for red
     */
    private void setStatus(String msg, boolean success) {
        statusLabel.setForeground(success ? new Color(0, 220, 0) : new Color(255, 80, 80));
        statusLabel.setText(msg);
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Shows the LIST card, clears form fields, and rebuilds the player list.
     */
    @Override
    public void onScreenShown() {
        cardLayout.show(cardPanel, "LIST");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        statusLabel.setText(" ");
        rebuildPlayerList();
    }

    /**
     * Rebuilds the player list from AccountManager.
     * Each button stores the selected username and flips to the FORM card.
     * Reinstalls the MenuKeyHandler after rebuilding.
     */
    private void rebuildPlayerList() {
        listPanel.removeAll();
        List<JComponent> navItems = new ArrayList<>();

        Map<String, PlayerProfile> accounts = screenManager.getAccountManager().getAllAccounts();
        for (String username : accounts.keySet()) {
            StyledButton playerBtn = new StyledButton(username, 350, 45);
            playerBtn.setMaximumSize(new Dimension(350, 45));
            playerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerBtn.addActionListener(e -> {
                selectedUsername = username;
                formTitleLabel.setText("Reset Password: " + username);
                statusLabel.setText(" ");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                cardLayout.show(cardPanel, "FORM");
                newPasswordField.requestFocusInWindow();
            });
            listPanel.add(playerBtn);
            listPanel.add(Box.createVerticalStrut(5));
            navItems.add(playerBtn);
        }

        listPanel.revalidate();
        listPanel.repaint();

        if (keyHandler != null) keyHandler.uninstall();
        keyHandler = new MenuKeyHandler(navItems);
        keyHandler.install(this);
    }
}
