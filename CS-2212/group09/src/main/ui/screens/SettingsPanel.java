package main.ui.screens;

import main.account.PlayerProfile;
import main.account.Settings;
import main.ui.AssetLoader;
import main.ui.SoundManager;
import main.ui.components.PixelLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Reusable audio settings panel providing volume, music, and SFX controls.
 * Embedded in PauseOverlay and refreshed with the current player profile each time the overlay becomes visible.
 *
 * @author Adam Porbanderwalla
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class SettingsPanel extends JPanel {

    private SoundManager soundManager;

    private PlayerProfile player;

    private Runnable onSettingChanged;

    private int volume;

    private boolean musicOn;

    private boolean sfxOn;

    private VolumeBarPanel barPanel;

    private PixelLabel valueLabel;

    private JCheckBox musicCheck;

    private JCheckBox sfxCheck;

    /**
     * Constructs a SettingsPanel initialised from the given player's saved settings.
     * Builds the full UI and wires all listeners so changes take effect immediately.
     * If player or its settings are null, defaults of volume 5, music on, and SFX on are used.
     *
     * @param soundManager     the SoundManager to receive audio control calls
     * @param player           the PlayerProfile whose Settings are displayed and updated, may be null
     * @param onSettingChanged a Runnable invoked after each setting change, or null if not needed
     */
    public SettingsPanel(SoundManager soundManager, PlayerProfile player, Runnable onSettingChanged) {
        this.soundManager = soundManager;
        this.player = player;
        this.onSettingChanged = onSettingChanged;

        Settings settings = player != null ? player.getSettings() : null;
        volume  = settings != null ? settings.getVolume()             : 5;
        musicOn = settings != null ? settings.isMusicEnabled()        : true;
        sfxOn   = settings != null ? settings.isSoundEffectsEnabled() : true;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // ── Title ──────────────────────────────────────────────────────────
        PixelLabel audioLabel = new PixelLabel("Audio", 24f, new Color(255, 215, 0));
        audioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Volume header: "Volume  [value]" ──────────────────────────────
        PixelLabel volLabel = new PixelLabel("Volume", 18f);

        valueLabel = new PixelLabel(String.valueOf(volume), 18f, new Color(255, 215, 0));

        JPanel headerRow = new JPanel();
        headerRow.setOpaque(false);
        headerRow.setLayout(new BoxLayout(headerRow, BoxLayout.X_AXIS));
        headerRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(220, 26));
        headerRow.add(volLabel);
        headerRow.add(Box.createHorizontalGlue());
        headerRow.add(valueLabel);

        // ── Volume control: "[-]  [bars]  [+]" ────────────────────────────
        JLabel minusBtn = makeArrowLabel("-");
        JLabel plusBtn  = makeArrowLabel("+");

        barPanel = new VolumeBarPanel(volume);

        JPanel controlRow = new JPanel();
        controlRow.setOpaque(false);
        controlRow.setLayout(new BoxLayout(controlRow, BoxLayout.X_AXIS));
        controlRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlRow.setMaximumSize(new Dimension(220, 28));
        controlRow.add(minusBtn);
        controlRow.add(Box.createHorizontalStrut(6));
        controlRow.add(barPanel);
        controlRow.add(Box.createHorizontalStrut(6));
        controlRow.add(plusBtn);

        // ── Checkboxes ─────────────────────────────────────────────────────
        musicCheck = makeCheckBox("Music", musicOn);
        sfxCheck   = makeCheckBox("SFX",   sfxOn);

        // ── Assemble ───────────────────────────────────────────────────────
        add(audioLabel);
        add(Box.createVerticalStrut(10));
        add(headerRow);
        add(Box.createVerticalStrut(4));
        add(controlRow);
        add(Box.createVerticalStrut(8));
        add(musicCheck);
        add(Box.createVerticalStrut(4));
        add(sfxCheck);

        // ── Listeners ──────────────────────────────────────────────────────
        minusBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { adjustVolume(-1); }
        });
        plusBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { adjustVolume(+1); }
        });

        musicCheck.addActionListener(e -> {
            musicOn = musicCheck.isSelected();
            soundManager.setMusicEnabled(musicOn);
            if (musicOn) soundManager.playMusic("in-game-bg.wav");
            if (this.player != null) this.player.getSettings().setMusicEnabled(musicOn);
            if (onSettingChanged != null) onSettingChanged.run();
        });

        sfxCheck.addActionListener(e -> {
            sfxOn = sfxCheck.isSelected();
            soundManager.setSFXEnabled(sfxOn);
            if (this.player != null) this.player.getSettings().setSoundEffectsEnabled(sfxOn);
            if (onSettingChanged != null) onSettingChanged.run();
        });
    }

    // ── Public API ─────────────────────────────────────────────────────────

    /**
     * Refreshes all UI controls from the given player's saved settings.
     * Called by PauseOverlay.refresh each time the overlay becomes visible.
     *
     * @param newPlayer the PlayerProfile to read current settings from, may be null
     */
    public void refresh(PlayerProfile newPlayer) {
        this.player = newPlayer;
        Settings s = newPlayer != null ? newPlayer.getSettings() : null;
        volume  = s != null ? s.getVolume() : 5;
        // Read live audio state from SoundManager so the checkboxes always
        // reflect what's actually playing, including icon toggles on other screens.
        musicOn = soundManager.isMusicEnabled();
        sfxOn   = soundManager.isSFXEnabled();

        barPanel.setValue(volume);
        valueLabel.setText(String.valueOf(volume));
        musicCheck.setSelected(musicOn);
        sfxCheck.setSelected(sfxOn);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    /**
     * Adjusts the current volume by the given signed delta, clamps to 0 to 10, and applies the change.
     * Called by the minus and plus label mouse listeners.
     *
     * @param delta signed integer to add to the current volume, typically plus or minus 1
     */
    private void adjustVolume(int delta) {
        int next = Math.max(0, Math.min(10, volume + delta));
        if (next == volume) return;
        volume = next;
        barPanel.setValue(volume);
        valueLabel.setText(String.valueOf(volume));
        soundManager.setVolume(volume);
        if (player != null) player.getSettings().setVolume(volume);
        if (onSettingChanged != null) onSettingChanged.run();
    }

    /**
     * Creates a styled clickable label used as a volume increment or decrement button.
     * Called during construction to build the minus and plus controls.
     *
     * @param text the character to display, typically "-" or "+"
     * @return a configured JLabel ready for use as an arrow button
     */
    private JLabel makeArrowLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AssetLoader.getPixelFont(20f));
        lbl.setForeground(Color.WHITE);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return lbl;
    }

    /**
     * Creates a styled JCheckBox with pixel-font text for use in the settings UI.
     * Called during construction to build the Music and SFX toggles.
     *
     * @param text     the display label for the checkbox
     * @param selected the initial selected state of the checkbox
     * @return a configured JCheckBox
     */
    private JCheckBox makeCheckBox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(AssetLoader.getPixelFont(18f));
        cb.setForeground(Color.WHITE);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);
        return cb;
    }

    // ── Inner component: painted bar strip ─────────────────────────────────

    /**
     * Custom painted panel rendering a row of 10 rectangular bars representing the current volume level.
     * Used inside SettingsPanel to visualise volume as filled and empty bar segments.
     */
    private static class VolumeBarPanel extends JPanel {

        /** Number of bars in the strip, corresponding to the maximum volume value. */
        private static final int BARS       = 10;

        /** Width in pixels of each individual bar. */
        private static final int BAR_W      = 10;

        /** Height in pixels of each individual bar. */
        private static final int BAR_H      = 18;

        /** Gap in pixels between adjacent bars. */
        private static final int BAR_GAP    = 3;

        /** Fill colour for bars whose index is below the current volume value. */
        private static final Color FILLED   = new Color(255, 255, 255);

        /** Fill colour for bars whose index is at or above the current volume value. */
        private static final Color EMPTY    = new Color(60, 60, 60);

        /** Border colour drawn around each bar. */
        private static final Color BORDER   = new Color(120, 120, 120);

        private int value;

        /**
         * Constructs a VolumeBarPanel with the specified initial volume value.
         * Sets preferred and maximum sizes based on bar constants.
         *
         * @param value initial volume level from 0 to 10
         */
        VolumeBarPanel(int value) {
            this.value = value;
            setOpaque(false);
            int w = BARS * BAR_W + (BARS - 1) * BAR_GAP;
            setPreferredSize(new Dimension(w, BAR_H + 2));
            setMaximumSize(new Dimension(w, BAR_H + 2));
        }

        /**
         * Updates the displayed volume level and schedules a repaint.
         *
         * @param v new volume level from 0 to 10
         */
        void setValue(int v) {
            this.value = v;
            repaint();
        }

        /**
         * Paints the 10-bar volume strip, filling bars up to the current value and leaving the rest empty.
         * Called by the Swing paint cycle whenever the value changes.
         *
         * @param g the Graphics context provided by Swing
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            int y = (getHeight() - BAR_H) / 2;
            for (int i = 0; i < BARS; i++) {
                int x = i * (BAR_W + BAR_GAP);
                g2.setColor(i < value ? FILLED : EMPTY);
                g2.fillRect(x, y, BAR_W, BAR_H);
                g2.setColor(BORDER);
                g2.drawRect(x, y, BAR_W - 1, BAR_H - 1);
            }
        }
    }
}
