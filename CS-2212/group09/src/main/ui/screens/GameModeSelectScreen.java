package main.ui.screens;

import main.modes.ModeType;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Screen for selecting a game mode: Normal, Timed, or Endless.
 * Normal navigates to {@link LevelSelectScreen}; Timed and Endless start a game at level 1.
 * Escape returns to {@link PlayerScreen}. Button positions scale with the panel size.
 * Keyboard navigation is handled by {@link MenuKeyHandler}. Shown by {@link ScreenManager}.
 *
 * @author Adam Porbanderwalla, Garv Sharma, Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class GameModeSelectScreen extends BackgroundPanel implements Refreshable {

    private MenuKeyHandler keyHandler;

    /**
     * Horizontal centre of the button column expressed as a fraction of the panel width
     * (0.5 = horizontally centred).
     */
    private static final double BTN_X_RATIO = 0.5;

    /** Button width expressed as a fraction of the panel width. */
    private static final double BTN_W_RATIO = 300.0 / 1024;

    /** Button height expressed as a fraction of the panel height. */
    private static final double BTN_H_RATIO = 65.0 / 768;

    /** Vertical position of the first button expressed as a fraction of the panel height. */
    private static final double BTN_Y_START_RATIO = 350.0 / 768;

    /** Vertical gap between consecutive buttons expressed as a fraction of the panel height. */
    private static final double BTN_GAP_RATIO = 85.0 / 768;

    private JComponent[] buttons;

    /**
     * Constructs the game-mode select screen with Normal, Timed, and Endless buttons.
     * Attaches action listeners, sets up keyboard navigation, registers Escape key shortcut,
     * and adds a ComponentListener to reposition buttons on resize.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public GameModeSelectScreen(ScreenManager screenManager) {
        super("bg/game-mode-select-bg.png");
        setLayout(null);

        ImageButton normalBtn = new ImageButton("normal-button");
        ImageButton timedBtn = new ImageButton("timed-button");
        ImageButton endlessBtn = new ImageButton("endless-button");

        buttons = new JComponent[]{normalBtn, timedBtn, endlessBtn};

        add(normalBtn);
        add(timedBtn);
        add(endlessBtn);

        normalBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.LEVEL_SELECT));
        timedBtn.addActionListener(e -> screenManager.startGame(ModeType.TIMED, 1));
        endlessBtn.addActionListener(e -> screenManager.startGame(ModeType.ENDLESS, 1));

        keyHandler = new MenuKeyHandler(Arrays.asList(normalBtn, timedBtn, endlessBtn));
        keyHandler.install(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                screenManager.showScreen(ScreenManager.PLAYER);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutButtons();
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
     * Repositions all mode buttons using ratio constants. Buttons are centred horizontally.
     * Called on each ComponentResized event. No effect if panel size is zero.
     */
    private void layoutButtons() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        int btnW = (int)(w * BTN_W_RATIO);
        int btnH = (int)(h * BTN_H_RATIO);
        int startX = (int)(w * BTN_X_RATIO) - btnW / 2;
        int startY = (int)(h * BTN_Y_START_RATIO);
        int gap = (int)(h * BTN_GAP_RATIO);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBounds(startX, startY + gap * i, btnW, btnH);
        }
    }
}
