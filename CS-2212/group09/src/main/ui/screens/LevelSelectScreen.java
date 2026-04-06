package main.ui.screens;

import main.account.PlayerProfile;
import main.modes.ModeType;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.StyledButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Level selection screen showing a 2x5 grid of level buttons (levels 1 to 10) for Normal mode.
 * Locked levels are disabled and labelled "X". Clicking an unlocked level starts a Normal game.
 * Back button and Escape return to {@link GameModeSelectScreen}.
 * Implements {@link main.ui.Refreshable} so the grid rebuilds on each visit. Shown by {@link ScreenManager}.
 *
 * @author Adam Porbanderwalla, Garv Sharma 
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class LevelSelectScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private StyledButton[] levelButtons = new StyledButton[10];
    private MenuKeyHandler keyHandler;
    private ImageButton backBtn;

    /**
     * Constructs the level-select screen with a 2x5 grid of level buttons and a Back button.
     * Attaches action listeners for starting Normal-mode games and calls rebuildKeyHandler()
     * for initial keyboard navigation setup.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public LevelSelectScreen(ScreenManager screenManager) {
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
            BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));
        contentPanel.setOpaque(false);

        PixelLabel titleLabel = new PixelLabel("Select Level", 48f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel gridPanel = new JPanel(new GridLayout(2, 5, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(600, 230));
        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 0; i < 10; i++) {
            final int level = i + 1;
            levelButtons[i] = new StyledButton(String.valueOf(level), 100, 100);
            levelButtons[i].addActionListener(e -> screenManager.startGame(ModeType.NORMAL, level));
            gridPanel.add(levelButtons[i]);
        }

        backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.MODE_SELECT));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(gridPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(backBtn);

        add(contentPanel);

        rebuildKeyHandler();
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Updates each level button's enabled state based on the current PlayerProfile,
     * then rebuilds keyboard navigation via rebuildKeyHandler().
     */
    @Override
    public void onScreenShown() {
        PlayerProfile player = screenManager.getAccountManager().getCurrentPlayer();
        if (player != null) {
            for (int i = 0; i < 10; i++) {
                boolean unlocked = player.isLevelUnlocked(i + 1);
                levelButtons[i].setEnabled(unlocked);
                levelButtons[i].setText(unlocked ? String.valueOf(i + 1) : "X");
            }
        }
        rebuildKeyHandler();
    }

    /**
     * Rebuilds the MenuKeyHandler to include only enabled level buttons plus the Back button.
     * Uninstalls any previous handler first to avoid duplicate bindings.
     * Also re-registers the Escape key to return to {@link GameModeSelectScreen}.
     * Called from the constructor and from onScreenShown() when unlock state may have changed.
     */
    private void rebuildKeyHandler() {
        List<JComponent> navigables = new ArrayList<>();
        for (StyledButton btn : levelButtons) {
            if (btn.isEnabled()) {
                navigables.add(btn);
            }
        }
        navigables.add(backBtn);
        if (keyHandler != null) keyHandler.uninstall();
        keyHandler = new MenuKeyHandler(navigables);
        keyHandler.install(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                screenManager.showScreen(ScreenManager.MODE_SELECT);
            }
        });
    }
}
