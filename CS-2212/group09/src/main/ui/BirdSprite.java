package main.ui;

import main.gameplay.WordTarget;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Tracks position, velocity, animation, and visual state for a single bird on screen.
 *
 * Each BirdSprite is paired with a {@link WordTarget} from the gameplay engine. The sprite owns
 * all rendering state while the engine's WordTarget owns the linguistic state. Created by GameScreen
 * whenever TargetManager spawns a new WordTarget. update() is called every frame, followed by
 * getImage() to obtain the frame to paint.
 *
 * @author Imad Tahir 
 */
public class BirdSprite {

    public enum SpawnDirection { LEFT, BOTTOM_LEFT, BOTTOM_CENTER }
    public enum VisualState { FLYING, STRUCK, FALLING }

    /** Seconds per wing-flap animation frame (approximately 6 fps). */
    private static final double ANIM_FRAME_DURATION = 0.17;

    /** Seconds the bird stays frozen after being struck before it begins falling. */
    private static final double STRUCK_DURATION = 0.5;

    /** Downward acceleration in pixels per second squared applied during the FALLING state. */
    private static final double GRAVITY = 400;

    private double x, y;
    private double vx, vy;
    private int birdType;               // 0=blue, 1=green, 2=red
    private VisualState state;
    private int animFrame;
    private double animTimer;
    private double stateTimer;
    private WordTarget target;
    private int flyingDirection;        // 0=diagonal, 1=upward, 2=horizontal

    /**
     * Constructs a BirdSprite and sets its initial position and velocity based on the spawn direction.
     * Called by GameScreen each time TargetManager spawns a new WordTarget.
     *
     * @param target    the WordTarget this sprite represents
     * @param birdType  bird colour index: 0=blue, 1=green, 2=red
     * @param direction the screen edge from which the bird spawns
     * @param screenW   game canvas width in pixels
     * @param screenH   game canvas height in pixels
     */
    public BirdSprite(WordTarget target, int birdType, SpawnDirection direction,
                      int screenW, int screenH) {
        this.target = target;
        this.birdType = birdType;

        this.state = VisualState.FLYING;
        this.animFrame = 0;
        this.animTimer = 0;
        this.stateTimer = 0;
        Random rng = new Random();
        switch (direction) {
            case LEFT:
                x = -40;
                y = 100 + rng.nextInt(Math.max(1, screenH / 2 - 100));
                vx = 80 + rng.nextInt(41);   // 80-120 px/s
                vy = -20 + rng.nextInt(41);  // -20 to +20
                flyingDirection = 0; // row 3
                break;
            case BOTTOM_LEFT:
                x = -40;
                y = screenH + 40;
                vx = 60 + rng.nextInt(41);   // 60-100
                vy = -(80 + rng.nextInt(41)); // -80 to -120
                flyingDirection = 1; // row 4
                break;
            case BOTTOM_CENTER:
                x = screenW / 2.0 - 100 + rng.nextInt(201); // center ± 100
                y = screenH + 40;
                vx = -10 + rng.nextInt(21);  // -10 to +10
                vy = -(100 + rng.nextInt(41)); // -100 to -140
                flyingDirection = 2; // row 5
                break;
        }
    }

    /**
     * Advances position, animation frame, and state timers by one frame.
     * Called every frame by GameScreen's render loop before painting.
     *
     * @param delta elapsed time since the last update in seconds
     */
    public void update(double delta) {
        stateTimer += delta;

        switch (state) {
            case FLYING:
                x += vx * delta;
                y += vy * delta;
                animTimer += delta;
                if (animTimer >= ANIM_FRAME_DURATION) {
                    animTimer -= ANIM_FRAME_DURATION;
                    animFrame = (animFrame + 1) % BirdSpriteSheet.FLYING_FRAMES;
                }
                break;

            case STRUCK:
                // Freeze in place for a moment
                if (stateTimer >= STRUCK_DURATION) {
                    transitionToFalling();
                }
                break;

            case FALLING:
                vy += GRAVITY * delta;
                y += vy * delta;
                break;
        }
    }

    /**
     * Transitions the sprite to the STRUCK state, freezing it at its current position.
     * Called by GameScreen when the player correctly types the word for this sprite.
     */
    public void strike() {
        state = VisualState.STRUCK;
        stateTimer = 0;
        vx = 0;
        vy = 0;
    }

    private void transitionToFalling() {
        state = VisualState.FALLING;
        stateTimer = 0;
        vx = 0;
        vy = 0; // gravity will accelerate
    }

    /**
     * Returns the correct sprite image for the current visual state, animation frame, and bird type.
     * Delegates to the appropriate method on {@link BirdSpriteSheet}. Called by GameScreen each paint pass.
     *
     * @param sheet the loaded BirdSpriteSheet to sample frames from
     * @return the BufferedImage for the current frame, or null if the sheet is not loaded
     */
    public BufferedImage getImage(BirdSpriteSheet sheet) {
        switch (state) {
            case FLYING:
                return sheet.getFlying(flyingDirection, birdType, animFrame);
            case STRUCK:
                return sheet.getStruck(birdType);
            case FALLING:
                return sheet.getFalling(birdType);
            default:
                return null;
        }
    }

    /**
     * Returns true if the sprite has moved outside the visible game canvas and should be removed.
     * Called by GameScreen after each update() call.
     *
     * @param screenW game canvas width in pixels
     * @param screenH game canvas height in pixels
     * @return true if the sprite is no longer within or near the canvas bounds
     */
    public boolean isOffScreen(int screenW, int screenH) {
        return y > screenH + 60 || x > screenW + 60 || x < -100 || y < -100;
    }

    // --- Getters ---

    /**
     * x-coordinate getter. Used by GameScreen to position the bird image during painting.
     *
     * @return x-coordinate in pixels
     */
    public double getX()              { return x; }

    /**
     * y-coordinate getter. Used by GameScreen to position the bird image during painting.
     *
     * @return y-coordinate in pixels
     */
    public double getY()              { return y; }

    /**
     * VisualState getter. Used by GameScreen to determine when a struck bird can be culled.
     *
     * @return the current VisualState
     */
    public VisualState getState()     { return state; }

    /**
     * WordTarget getter. Used by GameScreen to correlate keyboard input with the correct sprite.
     *
     * @return the associated WordTarget, or null if clearTarget() has been called
     */
    public WordTarget getTarget()     { return target; }

    /**
     * Detaches this sprite from its WordTarget. Called by GameScreen after the target is removed
     * from the engine's active list.
     */
    public void clearTarget()         { this.target = null; }
}
