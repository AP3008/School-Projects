package main.ui.screens;

import main.account.PlayerProfile;
import main.ui.AssetLoader;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.*;

/**
 * Post-login hub screen showing a welcome greeting and four navigation buttons:
 * Play Game, Player Stats, Logout, and Exit. Includes a music toggle icon.
 * Component positions scale proportionally with the panel size. Keyboard navigation
 * is provided by {@link MenuKeyHandler}. Implements {@link main.ui.Refreshable} so
 * {@link ScreenManager} can refresh the username and music state on each visit.
 *
 * Artificial Intelligence Tool: ChatGPT; programming: handling positions;
 * @author Garv Sharma, Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class PlayerScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private PixelLabel welcomeLabel;
    private MenuKeyHandler keyHandler;

    /** Horizontal position of the welcome label expressed as a fraction of the panel width. */
    private static final double LABEL_X_RATIO = 600.0 / 1024;

    /** Vertical position of the welcome label expressed as a fraction of the panel height. */
    private static final double LABEL_Y_RATIO = 200.0 / 768;

    /** Width of the welcome label expressed as a fraction of the panel width. */
    private static final double LABEL_W_RATIO = 350.0 / 1024;

    /** Height of the welcome label expressed as a fraction of the panel height. */
    private static final double LABEL_H_RATIO = 50.0 / 768;

    /** Horizontal position of the button column expressed as a fraction of the panel width. */
    private static final double BTN_X_RATIO = 650.0 / 1024;

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
     * Constructs the player screen with a welcome label, navigation buttons, and music toggle.
     * Attaches action listeners, configures keyboard navigation via {@link MenuKeyHandler},
     * and registers a ComponentListener to reposition elements on resize.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public PlayerScreen(ScreenManager screenManager) {
        super("bg/player-screen-bg.png");
        this.screenManager = screenManager;
        setLayout(null);

        welcomeLabel = new PixelLabel("Welcome, Player!", 36f, new Color(255, 215, 0));

        ImageButton playBtn = new ImageButton("play-game-button");
        ImageButton statsBtn = new ImageButton("player-stats");
        ImageButton logoutBtn = new ImageButton("logout-button");
        ImageButton exitBtn = new ImageButton("exit-button");

        buttons = new JComponent[]{playBtn, statsBtn, logoutBtn, exitBtn};

        add(welcomeLabel);
        add(playBtn);
        add(statsBtn);
        add(logoutBtn);
        add(exitBtn);

        playBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.MODE_SELECT));
        statsBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.PLAYER_STATS));
        logoutBtn.addActionListener(e -> screenManager.logout());
        exitBtn.addActionListener(e -> System.exit(0));

        keyHandler = new MenuKeyHandler(Arrays.asList(playBtn, statsBtn, logoutBtn, exitBtn));
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
                layoutComponents();
            }
        });
    }

    /**
     * Repositions the welcome label, navigation buttons, and music toggle icon using ratio constants.
     * Called on each ComponentResized event. No effect if panel size is zero.
     */
    private void layoutComponents() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        welcomeLabel.setBounds(
            (int)(w * LABEL_X_RATIO), (int)(h * LABEL_Y_RATIO),
            (int)(w * LABEL_W_RATIO), (int)(h * LABEL_H_RATIO)
        );

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
     * Updates the welcome label with the current player's username, syncs the music toggle,
     * and resets keyboard focus.
     */
    @Override
    public void onScreenShown() {
        if (screenManager.getAccountManager().getCurrentPlayer() != null) {
            String name = screenManager.getAccountManager().getCurrentPlayer().getUsername();
            welcomeLabel.setText("Welcome, " + name + "!");
        }
        musicOn = screenManager.getSoundManager().isMusicEnabled();
        musicToggle.setIcon(musicOn ? iconOn : iconOff);
        keyHandler.resetFocus();
    }
}
