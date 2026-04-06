package main.ui.screens;

import main.account.PlayerProfile;
import main.ui.AssetLoader;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.*;

/**
 * Main menu screen shown on application launch and after logout.
 * Displays five navigation buttons (Login, Tutorial, High Scores, Parental Controls, Exit)
 * and a music toggle icon. Button positions scale proportionally with the panel size.
 * Keyboard navigation is provided by {@link MenuKeyHandler}. Managed by {@link ScreenManager}.
 *
 *Artificial Intelligence Tool: Claude; Programming: implementing the audio & helping with button positioning debugging; 
 * @author Adam Porbanderwalla, Jaideep Singh 
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class MainMenuScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private MenuKeyHandler keyHandler;

    /** Horizontal position of the button column expressed as a fraction of the panel width. */
    private static final double BTN_X_RATIO = 700.0 / 1024;

    /** Vertical position of the first button expressed as a fraction of the panel height. */
    private static final double BTN_Y_START_RATIO = 280.0 / 768;

    /** Button width expressed as a fraction of the panel width. */
    private static final double BTN_W_RATIO = 250.0 / 1024;

    /** Button height expressed as a fraction of the panel height. */
    private static final double BTN_H_RATIO = 55.0 / 768;

    /** Vertical gap between consecutive buttons expressed as a fraction of the panel height. */
    private static final double BTN_GAP_RATIO = 70.0 / 768;

    /** Horizontal position of the music-toggle icon expressed as a fraction of the panel width. */
    private static final double ICON_X_RATIO = 935.0 / 1024;

    /** Vertical position of the music-toggle icon expressed as a fraction of the panel height. */
    private static final double ICON_Y_RATIO = 700.0 / 768;

    /** Pixel size (width and height) used when scaling the music-toggle icon. */
    private static final int ICON_SIZE = 50;

    private JComponent[] buttons;
    private JLabel musicToggle;
    private ImageIcon iconOn;
    private ImageIcon iconOff;
    private boolean musicOn = true;

    /**
     * Constructs the main menu screen, sets up navigation buttons, attaches action listeners,
     * configures keyboard navigation via {@link MenuKeyHandler}, loads music toggle icons,
     * and registers a ComponentListener to reposition elements on resize.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public MainMenuScreen(ScreenManager screenManager) {
        super("bg/home-screen-bg.png");
        this.screenManager = screenManager;
        setLayout(null);

        ImageButton loginBtn = new ImageButton("login-button");
        ImageButton tutorialBtn = new ImageButton("tutorial-button");
        ImageButton highScoresBtn = new ImageButton("high-scores-button");
        ImageButton parentalBtn = new ImageButton("parental-control-button");
        ImageButton exitBtn = new ImageButton("exit-button");

        buttons = new JComponent[]{loginBtn, tutorialBtn, highScoresBtn, parentalBtn, exitBtn};

        add(loginBtn);
        add(tutorialBtn);
        add(highScoresBtn);
        add(parentalBtn);
        add(exitBtn);

        loginBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.LOGIN));
        tutorialBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.TUTORIAL));
        highScoresBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.HIGH_SCORES));
        parentalBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.PARENTAL_PIN));
        exitBtn.addActionListener(e -> System.exit(0));

        keyHandler = new MenuKeyHandler(Arrays.asList(loginBtn, tutorialBtn, highScoresBtn, parentalBtn, exitBtn));
        keyHandler.install(this);

        BufferedImage imgOn = AssetLoader.loadImage("ui/music-on-icon.png");
        BufferedImage imgOff = AssetLoader.loadImage("ui/music-off-icon.png");
        if (imgOn != null) iconOn = new ImageIcon(AssetLoader.scaleImage(imgOn, ICON_SIZE, ICON_SIZE));
        if (imgOff != null) iconOff = new ImageIcon(AssetLoader.scaleImage(imgOff, ICON_SIZE, ICON_SIZE));

        musicToggle = new JLabel(iconOn);
        musicToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        musicToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleMusic();
            }
        });
        add(musicToggle);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutButtons();
            }
        });
    }

    /**
     * Repositions all navigation buttons and the music toggle icon using ratio constants.
     * Called on each ComponentResized event. No effect if panel size is zero.
     */
    private void layoutButtons() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        int btnW = (int)(w * BTN_W_RATIO);
        int btnH = (int)(h * BTN_H_RATIO);
        int startX = (int)(w * BTN_X_RATIO);
        int startY = (int)(h * BTN_Y_START_RATIO);
        int gap = (int)(h * BTN_GAP_RATIO);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBounds(startX, startY + gap * i, btnW, btnH);
        }

        musicToggle.setBounds(
            (int)(w * ICON_X_RATIO),
            (int)(h * ICON_Y_RATIO),
            ICON_SIZE, ICON_SIZE
        );
    }

    /**
     * Toggles background music on or off, updates SoundManager, persists the preference
     * to the current PlayerProfile via AccountManager, and updates the toggle icon.
     */
    private void toggleMusic() {
        musicOn = !musicOn;
        screenManager.getSoundManager().setMusicEnabled(musicOn);
        PlayerProfile player = screenManager.getAccountManager().getCurrentPlayer();
        if (player != null) {
            player.getSettings().setMusicEnabled(musicOn);
            screenManager.getAccountManager().saveAll();
        }
        if (musicOn) {
            musicToggle.setIcon(iconOn);
            screenManager.getSoundManager().playMusic("01. Title BGM.wav");
        } else {
            musicToggle.setIcon(iconOff);
        }
    }

    /**
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Syncs the music toggle icon with the current SoundManager state and resets keyboard focus.
     */
    @Override
    public void onScreenShown() {
        musicOn = screenManager.getSoundManager().isMusicEnabled();
        musicToggle.setIcon(musicOn ? iconOn : iconOff);
        keyHandler.resetFocus();
    }

}
