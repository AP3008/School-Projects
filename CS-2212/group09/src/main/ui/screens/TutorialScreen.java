package main.ui.screens;

import main.ui.AssetLoader;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Read-only tutorial screen explaining how to play KeyHunter.
 * Displays static instructions covering game mechanics, all three modes, streak bonuses,
 * and power-ups in a scrollable text area. Back button and Escape return to {@link MainMenuScreen}.
 * Implements {@link main.ui.Refreshable} to reset keyboard focus on each visit. Shown by {@link ScreenManager}.
 *
 * Artificial Intelligence Tool: Claude; Writing: Used to generate the tutorial message;
 * @author Adam Porbanderwalla
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class TutorialScreen extends BackgroundPanel implements Refreshable {

    private MenuKeyHandler keyHandler;

    /**
     * Constructs the tutorial screen with a scrollable read-only text area and a Back button.
     * Registers the Escape key to return to {@link MainMenuScreen}. Tutorial content is static.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public TutorialScreen(ScreenManager screenManager) {
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
            BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));
        contentPanel.setOpaque(false);

        PixelLabel titleLabel = new PixelLabel("Tutorial", 48f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea tutorialText = new JTextArea();
        tutorialText.setFont(AssetLoader.getPixelFont(20f));
        tutorialText.setBackground(new Color(30, 30, 30));
        tutorialText.setForeground(Color.WHITE);
        tutorialText.setEditable(false);
        tutorialText.setFocusable(false);
        tutorialText.setLineWrap(true);
        tutorialText.setWrapStyleWord(true);
        tutorialText.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tutorialText.setText(
            "Welcome to KeyHunter!\n\n" +
            "HOW TO PLAY:\n" +
            "Words will appear on screen attached to flying ducks.\n" +
            "Type the words correctly before they escape!\n" +
            "Each correct letter turns green. Complete a word to\n" +
            "shoot down the duck and score points.\n\n" +
            "GAME MODES:\n" +
            "- Normal: Progress through 10 levels of increasing\n" +
            "  difficulty. You have 3 lives - lose one each time\n" +
            "  a word escapes.\n" +
            "- Timed: Score as many points as you can in 60\n" +
            "  seconds! No lives to worry about.\n" +
            "- Endless: Play until you run out of lives. Words\n" +
            "  get progressively harder.\n\n" +
            "STREAK BONUS:\n" +
            "Complete 10 words in a row to activate DOUBLE POINTS\n" +
            "for 10 seconds! The streak bar shows your progress.\n\n" +
            "TIME SLOWDOWN:\n" +
            "Some ducks carry a special powerup shown by blue\n" +
            "text. Type their word to activate TIME SLOWDOWN for\n" +
            "5 seconds - all ducks slow to half speed!\n\n" +
            "EXTRA LIFE:\n" +
            "Rarely, a duck may carry an extra life powerup shown\n" +
            "by pink text. Type their word to gain +1 life! This\n" +
            "powerup only appears in Normal and Endless modes when\n" +
            "you have fewer than 4 lives.\n\n" +
            "CONTROLS:\n" +
            "- Type letters to match words on screen\n" +
            "- ESC to pause during gameplay\n" +
            "- Arrow keys to navigate menus\n" +
            "- ENTER to select menu options"
        );

        JScrollPane scrollPane = new JScrollPane(tutorialText);
        scrollPane.setPreferredSize(new Dimension(550, 380));
        scrollPane.setMaximumSize(new Dimension(550, 380));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.MAIN_MENU));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(backBtn);

        add(contentPanel);

        keyHandler = new MenuKeyHandler(Arrays.asList(backBtn));
        keyHandler.install(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                screenManager.showScreen(ScreenManager.MAIN_MENU);
            }
        });
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible. Resets keyboard focus.
     * No data refresh needed as tutorial content is static.
     */
    @Override
    public void onScreenShown() {
        keyHandler.resetFocus();
    }
}
