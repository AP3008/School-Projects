package main.ui.screens;

import main.account.PlayerProfile;
import main.engine.GameEngine;
import main.engine.GameSession;
import main.engine.GameState;
import main.engine.RunResult;
import main.engine.ScoreManager;
import main.gameplay.WordTarget;
import main.modes.ModeType;
import main.ui.AssetLoader;
import main.ui.BirdSprite;
import main.ui.BirdSpriteSheet;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Primary gameplay panel for KeyHunter, running the game loop at approximately 60fps.
 * Renders bird sprites, word labels, and the HUD, and is created and shown by ScreenManager.
 *
 * @author Adam Porbanderwalla, Garv Sharma, Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class GameScreen extends BackgroundPanel implements ActionListener {

    private ScreenManager screenManager;
    private Timer gameTimer;
    private long lastNanoTime;
    private Random random;
    private PauseOverlay pauseOverlay;
    private LevelPassedOverlay levelPassedOverlay;
    private boolean paused;
    private boolean levelPassedShowing;
    private int lastLevel;
    private boolean lastDoublePointsState;
    private boolean lastSlowdownState;
    private int lastLivesCount;
    private BufferedImage heartImg;
    private BirdSpriteSheet spriteSheet;
    private List<BirdSprite> activeBirds;

    /** Horizontal padding in pixels added around word labels drawn beneath birds. */
    private static final int TARGET_PADDING = 20;

    /** Width and height in pixels at which each bird sprite frame is drawn on screen. */
    private static final int BIRD_DRAW_SIZE = 70;

    /** Array of all possible spawn direction values for bird spawning. */
    private static final BirdSprite.SpawnDirection[] DIRECTIONS = BirdSprite.SpawnDirection.values();

    /**
     * Constructs a new GameScreen and wires all sub-components.
     * Initialises assets, overlays, the resize listener, the keyboard handler, and the 16ms game
     * loop timer without starting it. Called once by ScreenManager at application startup.
     * 
     * 
     * @param screenManager the application ScreenManager used to obtain engine and manager references
     */
    public GameScreen(ScreenManager screenManager) {
        super("bg/generic-menu-bg.png");
        this.screenManager = screenManager;
        this.activeBirds = new ArrayList<>();
        this.random = new Random();
        this.paused = false;
        this.levelPassedShowing = false;

        setLayout(null);
        setFocusable(true);

        heartImg = AssetLoader.loadImage("sprites/pixel-heart.png");
        spriteSheet = new BirdSpriteSheet();
        spriteSheet.load();

        pauseOverlay = new PauseOverlay(this);
        pauseOverlay.setVisible(false);
        add(pauseOverlay);

        levelPassedOverlay = new LevelPassedOverlay(this);
        levelPassedOverlay.setVisible(false);
        add(levelPassedOverlay);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int pw = 400, ph = 400;
                pauseOverlay.setBounds((getWidth() - pw) / 2, (getHeight() - ph) / 2, pw, ph);
                levelPassedOverlay.setBounds((getWidth() - pw) / 2, (getHeight() - ph) / 2, pw, ph);
            }
        });

        gameTimer = new Timer(16, this);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (paused || levelPassedShowing) return;
                char c = e.getKeyChar();
                if (c != KeyEvent.CHAR_UNDEFINED && !Character.isISOControl(c)) {
                    GameEngine engine = screenManager.getGameEngine();
                    engine.handleKeystroke(c);
                    repaint();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !levelPassedShowing) {
                    togglePause();
                }
            }
        });
    }

    /**
     * Resets transient state and starts the game loop.
     * Clears active birds, hides overlays, snapshots engine state, starts background music, and
     * requests keyboard focus. Called by ScreenManager after a new GameSession is configured.
     */
    public void startGameLoop() {
        activeBirds.clear();
        paused = false;
        levelPassedShowing = false;
        pauseOverlay.setVisible(false);
        levelPassedOverlay.setVisible(false);
        lastNanoTime = System.nanoTime();
        GameEngine engine = screenManager.getGameEngine();
        lastLevel = engine.getSession().getLevel();
        lastDoublePointsState = engine.getScoreManager().isDoublePointsActive();
        lastSlowdownState = engine.getScoreManager().isSlowdownActive();
        lastLivesCount = engine.getSession().getLives();
        screenManager.getSoundManager().playMusic("in-game-bg.wav");
        gameTimer.start();
        requestFocusInWindow();
    }


    /**
     * Game loop tick callback invoked by the 16ms Swing Timer.
     * Advances engine logic, spawns and updates bird sprites, detects game over and level
     * transitions, plays audio cues, and triggers a repaint.
     *
     * @param e the action event fired by the timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused) return;

        long now = System.nanoTime();
        double delta = (now - lastNanoTime) / 1_000_000_000.0;
        lastNanoTime = now;
        delta = Math.min(delta, 0.05);

        GameEngine engine = screenManager.getGameEngine();
        engine.update(delta);

        List<WordTarget> engineTargets = engine.getActiveTargets();

        // --- Spawn birds for newly appeared targets ---
        Set<WordTarget> tracked = new HashSet<>();
        for (BirdSprite bird : activeBirds) {
            if (bird.getTarget() != null) {
                tracked.add(bird.getTarget());
            }
        }
        for (WordTarget t : engineTargets) {
            if (!tracked.contains(t)) {
                int birdType = random.nextInt(BirdSpriteSheet.BIRD_TYPES);
                BirdSprite.SpawnDirection dir = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
                activeBirds.add(new BirdSprite(t, birdType, dir, getWidth(), getHeight()));
            }
        }

        // --- Detect completed / escaped targets → trigger death or removal ---
        Set<WordTarget> activeSet = new HashSet<>(engineTargets);
        for (BirdSprite bird : activeBirds) {
            if (bird.getTarget() != null && bird.getState() == BirdSprite.VisualState.FLYING) {
                if (!activeSet.contains(bird.getTarget())) {
                    // Target was removed by engine
                    if (bird.getTarget().isCompleted()) {
                        // Word typed correctly → strike the bird
                        bird.strike();
                        screenManager.getSoundManager().playSFX("99 - Dead Duck Falls (SFX).wav");
                    }
                    // else: escaped / timed out — just mark for removal
                    bird.clearTarget();
                }
            }
        }

        // --- Update all birds (half speed if slowdown active) ---
        double birdDelta = engine.getScoreManager().isSlowdownActive() ? delta * 0.5 : delta;
        for (BirdSprite bird : activeBirds) {
            bird.update(birdDelta);
        }

        // --- Remove birds that are done ---
        Iterator<BirdSprite> it = activeBirds.iterator();
        while (it.hasNext()) {
            BirdSprite bird = it.next();
            // Escaped birds with no target and still FLYING → remove immediately
            if (bird.getTarget() == null && bird.getState() == BirdSprite.VisualState.FLYING) {
                it.remove();
                continue;
            }
            // Falling birds that went off-screen → play land SFX and remove
            if (bird.getState() == BirdSprite.VisualState.FALLING && bird.isOffScreen(getWidth(), getHeight())) {
                screenManager.getSoundManager().playSFX("99 - Dead Duck Lands (SFX).wav");
                it.remove();
            }
        }

        GameSession session = engine.getSession();
        if (session.getState() == GameState.GAME_OVER) {
            gameTimer.stop();
            screenManager.getSoundManager().stopMusic();
            RunResult result = engine.endSession();
            if (session.getModeType() == ModeType.TIMED) {
                screenManager.getSoundManager().playSFX("06. Clear.wav");
                screenManager.showLevelComplete(result);
            } else {
                screenManager.getSoundManager().playSFX("10. Game Over.wav");
                screenManager.showGameOver(result);
            }
            return;
        }

        // Detect level clear (Normal mode)
        int currentLevel = session.getLevel();
        if (currentLevel != lastLevel && session.getModeType() == ModeType.NORMAL) {
            if (lastLevel == 10) {
                gameTimer.stop();
                screenManager.getSoundManager().stopMusic();
                screenManager.getSoundManager().playSFX("06. Clear.wav");
                RunResult result = engine.endSession();
                screenManager.showCongratulations(result);
                lastLevel = currentLevel;
                return;
            } else {
                screenManager.getSoundManager().stopMusic();
                screenManager.getSoundManager().playSFX("06. Clear.wav");
                showLevelPassed();
                lastLevel = currentLevel;
            }
        }

        // Detect double points activation
        boolean doubleNow = engine.getScoreManager().isDoublePointsActive();
        if (doubleNow && !lastDoublePointsState) {
            screenManager.getSoundManager().playSFX("Double-points-active.wav");
        }
        lastDoublePointsState = doubleNow;

        // Detect slowdown activation
        boolean slowdownNow = engine.getScoreManager().isSlowdownActive();
        if (slowdownNow && !lastSlowdownState) {
            screenManager.getSoundManager().playSFX("Double-points-active.wav");
        }
        lastSlowdownState = slowdownNow;

        // Detect extra life gained
        int currentLives = session.getLives();
        if (currentLives > lastLivesCount) {
            screenManager.getSoundManager().playSFX("Double-points-active.wav");
        }
        lastLivesCount = currentLives;

        repaint();
    }

    /**
     * Renders the current game frame, delegating to drawBirds and drawHUD.
     * Called by the Swing repaint cycle each tick.
     *
     * @param g the Graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        GameEngine engine = screenManager.getGameEngine();
        if (engine.getSession() == null) return;

        drawBirds(g2, engine);
        drawHUD(g2, engine);
    }

    /**
     * Draws all active bird sprites and their word labels onto the game panel.
     * Called from paintComponent each frame.
     *
     * @param g2     the Graphics2D context to draw into
     * @param engine the active GameEngine providing session data
     */
    private void drawBirds(Graphics2D g2, GameEngine engine) {
        Font wordFont = AssetLoader.getPixelFont(24f);

        for (BirdSprite bird : activeBirds) {
            int bx = (int) bird.getX();
            int by = (int) bird.getY();

            // Draw bird sprite
            BufferedImage frame = bird.getImage(spriteSheet);
            if (frame != null) {
                g2.drawImage(frame, bx, by, BIRD_DRAW_SIZE, BIRD_DRAW_SIZE, null);
            }

            // Draw word label (only while FLYING or briefly during STRUCK)
            WordTarget target = bird.getTarget();
            if (target != null && bird.getState() == BirdSprite.VisualState.FLYING) {
                drawWordLabel(g2, wordFont, target, bx, by + BIRD_DRAW_SIZE + 4, target.hasSlowdownPowerup(), target.hasExtraLifePowerup());
            } else if (bird.getState() == BirdSprite.VisualState.STRUCK) {
                // Show the word briefly with a flash during struck
                // (target was cleared, but we can still show a visual cue)
            }
        }
    }

    /**
     * Draws the word label beneath a bird, including a TTL progress bar and per-character colour coding.
     * Called from drawBirds for each flying bird with a live target.
     *
     * @param g2                  the Graphics2D context to draw into
     * @param font                pixel font at the desired size for word text
     * @param target              the WordTarget whose word is displayed
     * @param x                   left edge in pixels of the label bounding box
     * @param y                   top edge in pixels of the label bounding box
     * @param hasSlowdownPowerup  true if untyped characters should be drawn in blue
     * @param hasExtraLifePowerup true if untyped characters should be drawn in pink
     */
    private void drawWordLabel(Graphics2D g2, Font font, WordTarget target, int x, int y, boolean hasSlowdownPowerup, boolean hasExtraLifePowerup) {
        String text = target.getWord().getText();
        int progress = target.getProgressIndex();

        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);
        int textH = fm.getHeight();
        int boxW = textW + TARGET_PADDING * 2;
        int boxH = textH + TARGET_PADDING;

        // TTL bar above word
        double ttlRatio = 1.0 - (target.getTimeAlive() / target.getTtl());
        ttlRatio = Math.max(0, Math.min(1, ttlRatio));
        int barW = boxW;
        int barH = 4;
        int barY = y - barH - 3;
        g2.setColor(new Color(40, 40, 40, 150));
        g2.fillRect(x, barY, barW, barH);
        Color barColor = ttlRatio > 0.5 ? new Color(0, 200, 0)
                : ttlRatio > 0.25 ? new Color(255, 200, 0)
                : new Color(255, 50, 50);
        g2.setColor(barColor);
        g2.fillRect(x, barY, (int) (barW * ttlRatio), barH);

        // Dark rounded background behind text
        g2.setColor(new Color(20, 20, 20, 200));
        g2.fillRoundRect(x, y, boxW, boxH, 8, 8);
        g2.setColor(new Color(80, 80, 80));
        g2.drawRoundRect(x, y, boxW, boxH, 8, 8);

        // Draw text: typed chars green, remaining white
        int textX = x + TARGET_PADDING;
        int textY = y + TARGET_PADDING / 2 + fm.getAscent();
        for (int i = 0; i < text.length(); i++) {
            Color untyped;
            if (hasSlowdownPowerup) {
                untyped = new Color(80, 160, 255);
            } else if (hasExtraLifePowerup) {
                untyped = new Color(255, 105, 180);
            } else {
                untyped = Color.WHITE;
            }
            g2.setColor(i < progress ? new Color(0, 220, 0) : untyped);
            String ch = String.valueOf(text.charAt(i));
            g2.drawString(ch, textX, textY);
            textX += fm.stringWidth(ch);
        }
    }

    /**
     * Draws the HUD bar along the top of the screen, showing score, mode info, high score,
     * lives, streak or power-up indicators, and WPM. Called from paintComponent each frame.
     *
     * @param g2     the Graphics2D context to draw into
     * @param engine the active GameEngine providing session and score manager data
     */
    private void drawHUD(Graphics2D g2, GameEngine engine) {
        GameSession session = engine.getSession();
        ScoreManager scoreManager = engine.getScoreManager();

        // HUD bar background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, getWidth(), 70);

        Font hudFont = AssetLoader.getPixelFont(28f);
        Font smallFont = AssetLoader.getPixelFont(20f);
        g2.setFont(hudFont);

        // Score (left)
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + session.getScore(), 20, 35);

        // Mode info (center)
        String modeInfo = "";
        ModeType mode = session.getModeType();
        if (mode == ModeType.NORMAL) {
            modeInfo = "Level " + session.getLevel();
        } else if (mode == ModeType.TIMED) {
            long elapsed = session.getElapsedTime();
            int remaining = Math.max(0, 60 - (int) (elapsed / 1000));
            modeInfo = "Time: " + remaining + "s";
        } else if (mode == ModeType.ENDLESS) {
            modeInfo = "Endless";
        }
        FontMetrics fm = g2.getFontMetrics();
        int modeX = (getWidth() - fm.stringWidth(modeInfo)) / 2;
        g2.drawString(modeInfo, modeX, 35);

        // High score for current mode (centered below mode info)
        PlayerProfile player = session.getPlayer();
        if (player != null) {
            Font hsFont = AssetLoader.getPixelFont(18f);
            g2.setFont(hsFont);
            int modeHighScore = player.getStats().getHighScoreForMode(mode);
            String hsText = "High Score: " + modeHighScore;
            FontMetrics hsFm = g2.getFontMetrics();
            int hsX = (getWidth() - hsFm.stringWidth(hsText)) / 2;
            g2.setColor(new Color(255, 215, 0));
            g2.drawString(hsText, hsX, 52);
        }

        // Lives (right) - heart sprites (skip for Timed mode)
        if (mode != ModeType.TIMED) {
            int heartSize = 28;
            int heartsX = getWidth() - 20 - (session.getLives() * (heartSize + 5));
            for (int i = 0; i < session.getLives(); i++) {
                if (heartImg != null) {
                    g2.drawImage(heartImg, heartsX + i * (heartSize + 5), 8, heartSize, heartSize, null);
                } else {
                    g2.setColor(new Color(255, 50, 50));
                    g2.fillOval(heartsX + i * (heartSize + 5), 8, heartSize, heartSize);
                }
            }
        }

        // Streak bar (bottom of HUD)
        g2.setFont(smallFont);
        int streak = scoreManager.getStreak();
        boolean doubleActive = scoreManager.isDoublePointsActive();

        boolean slowdownActive = scoreManager.isSlowdownActive();

        if (doubleActive || slowdownActive) {
            FontMetrics smFmInd = g2.getFontMetrics();
            int indicatorX = 20;
            if (doubleActive) {
                g2.setColor(new Color(255, 215, 0));
                g2.drawString("DOUBLE POINTS!", indicatorX, 60);
                indicatorX += smFmInd.stringWidth("DOUBLE POINTS!") + 20;
            }
            if (slowdownActive) {
                g2.setColor(new Color(100, 180, 255));
                g2.drawString("TIME SLOWDOWN!", indicatorX, 60);
            }
        } else {
            g2.setColor(new Color(60, 60, 60));
            g2.fillRect(20, 50, 200, 12);
            int streakFill = (int) ((streak / 10.0) * 200);
            g2.setColor(new Color(255, 165, 0));
            g2.fillRect(20, 50, streakFill, 12);
            g2.setColor(new Color(180, 180, 180));
            g2.drawString("Streak: " + streak + "/10", 230, 62);
        }

        // WPM (right side)
        double elapsedMinutes = session.getElapsedTime() / 60000.0;
        double wpm = (elapsedMinutes > 0) ? engine.getAccuracyTracker().getWordsCorrect() / elapsedMinutes : 0;
        String wpmText = String.format("WPM: %.1f", wpm);
        g2.setFont(smallFont);
        FontMetrics smFm = g2.getFontMetrics();
        g2.setColor(new Color(100, 200, 255));
        g2.drawString(wpmText, getWidth() - smFm.stringWidth(wpmText) - 20, 62);
    }

    /**
     * Toggles the pause state of the game, showing or hiding the PauseOverlay.
     * Called by the ESC key handler and accessible to PauseOverlay for indirect use.
     */
    public void togglePause() {
        GameEngine engine = screenManager.getGameEngine();
        if (paused) {
            resumeGame();
        } else {
            paused = true;
            engine.pause();
            screenManager.getSoundManager().playSFX("99 - Pause (SFX).wav");
            pauseOverlay.refresh();
            pauseOverlay.setVisible(true);
            pauseOverlay.requestFocusInWindow();
            repaint();
        }
    }

    /**
     * Resumes a paused game, hiding the PauseOverlay and restoring keyboard focus.
     * Called by the Resume button inside PauseOverlay and by togglePause.
     */
    public void resumeGame() {
        paused = false;
        screenManager.getGameEngine().resume();
        pauseOverlay.setVisible(false);
        lastNanoTime = System.nanoTime();
        requestFocusInWindow();
        repaint();
    }

    /**
     * Pauses the game and shows the LevelPassedOverlay after a Normal-mode level is cleared.
     * Called from actionPerformed when a level transition is detected before level 10 completes.
     */
    public void showLevelPassed() {
        paused = true;
        levelPassedShowing = true;
        screenManager.getGameEngine().pause();
        screenManager.getAccountManager().saveAll();
        levelPassedOverlay.setVisible(true);
        levelPassedOverlay.requestFocusInWindow();
        repaint();
    }

    /**
     * Transitions from the level-passed state into the next Normal-mode level.
     * Clears birds, resets engine counters, resumes music, and returns keyboard focus.
     * Called by the Next Level button inside LevelPassedOverlay.
     */
    public void resumeFromLevelPassed() {
        levelPassedShowing = false;
        paused = false;
        activeBirds.clear();
        screenManager.getGameEngine().getSession().reset();
        screenManager.getGameEngine().getScoreManager().reset();
        screenManager.getGameEngine().getAccuracyTracker().reset();
        lastDoublePointsState = false;
        lastSlowdownState = false;
        screenManager.getGameEngine().resume();
        levelPassedOverlay.setVisible(false);
        lastNanoTime = System.nanoTime();
        screenManager.getSoundManager().playMusic("in-game-bg.wav");
        requestFocusInWindow();
        repaint();
    }

    /**
     * Stops the game loop and navigates back to the player screen.
     * Called by the Quit button in both PauseOverlay and LevelPassedOverlay.
     */
    public void exitToMenu() {
        gameTimer.stop();
        screenManager.getSoundManager().stopMusic();
        screenManager.getGameEngine().endSession();
        screenManager.getAccountManager().saveAll();
        screenManager.showScreen(ScreenManager.PLAYER);
    }

    /**
     * Returns the ScreenManager that owns this screen.
     * Used by child overlays to reach the engine and managers without a direct reference.
     *
     * @return the ScreenManager associated with this screen
     */
    public ScreenManager getScreenManager() {
        return screenManager;
    }
}
