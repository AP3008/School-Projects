package main.ui;

import java.awt.image.BufferedImage;

/**
 * Extracts and caches individual bird sprite frames from the duckhunt sprite sheet PNG.
 *
 * Loads the sheet via {@link AssetLoader#loadImage(String)} so the file is read from disk only once.
 * Consumed exclusively by {@link BirdSprite}, which calls getFlying, getStruck, and getFalling
 * every render frame to obtain the correct image for the sprite's current animation state.
 * Artificial Intelligence Tool: Claude; Debugging: Helped with debugging my code, and properly implementing sprite sheet;
 *
 * @author Adam Porbanderwalla
 */
public class BirdSpriteSheet {

    private static final String SHEET_PATH = "sprites/duckhunt_various_sprites_sheet.png";

    private static final int[] GROUP_X = {0, 130, 260};
    private static final int GROUP_WIDTH = 113;
    private static final int FRAMES_PER_GROUP = 3;

    private static final int[] ROW3 = {119, 28}; // flying direction 1
    private static final int[] ROW4 = {157, 31}; // flying direction 2
    private static final int[] ROW5 = {197, 31}; // flying direction 3

    private static final int ROW6_Y = 237;
    private static final int ROW6_H = 30;
    private static final int[][] HIT_FALL = {
        {1,  31, 48,  18},   // blue
        {131, 31, 178, 18},  // green
        {261, 31, 308, 18},  // red
    };

    /** Number of bird colour variants in the sheet (blue, green, red). */
    public static final int BIRD_TYPES = 3;

    /** Number of animation frames per flying direction per bird type. */
    public static final int FLYING_FRAMES = 3;

    private static final int FLYING_DIRS = 3;

    private BufferedImage[][][] flying; // [direction][birdType][frame]
    private BufferedImage[] struck;     // [birdType]
    private BufferedImage[] falling;    // [birdType]

    private boolean loaded;

    /**
     * Parses the sprite sheet and populates the internal frame arrays.
     * Idempotent: returns immediately if already loaded. Called by GameScreen during initialisation.
     */
    public void load() {
        if (loaded) return;

        BufferedImage sheet = AssetLoader.loadImage(SHEET_PATH);
        if (sheet == null) {
            System.err.println("BirdSpriteSheet: failed to load " + SHEET_PATH);
            return;
        }

        flying = new BufferedImage[FLYING_DIRS][][];
        flying[0] = extractFlyingRow(sheet, ROW3);
        flying[1] = extractFlyingRow(sheet, ROW4);
        flying[2] = extractFlyingRow(sheet, ROW5);

        struck = new BufferedImage[BIRD_TYPES];
        falling = new BufferedImage[BIRD_TYPES];
        for (int b = 0; b < BIRD_TYPES; b++) {
            int hx = HIT_FALL[b][0], hw = HIT_FALL[b][1];
            int fx = HIT_FALL[b][2], fw = HIT_FALL[b][3];
            struck[b]  = sheet.getSubimage(hx, ROW6_Y, hw, ROW6_H);
            falling[b] = sheet.getSubimage(fx, ROW6_Y, fw, ROW6_H);
        }

        loaded = true;
    }

    /**
     * Extracts all bird-type and animation-frame sub-images from a single horizontal row.
     * Used internally by load() to process each of the three flying-direction rows.
     *
     * @param sheet  the full sprite sheet image
     * @param rowDef a two-element array {y, height} describing the row to extract
     * @return a [BIRD_TYPES][FLYING_FRAMES] array of sub-images
     */
    private BufferedImage[][] extractFlyingRow(BufferedImage sheet, int[] rowDef) {
        int y = rowDef[0];
        int h = rowDef[1];
        int cellW = GROUP_WIDTH / FRAMES_PER_GROUP;

        BufferedImage[][] frames = new BufferedImage[BIRD_TYPES][FLYING_FRAMES];
        for (int b = 0; b < BIRD_TYPES; b++) {
            for (int f = 0; f < FRAMES_PER_GROUP; f++) {
                int x = GROUP_X[b] + f * cellW;
                int w = (f < FRAMES_PER_GROUP - 1) ? cellW : (GROUP_WIDTH - f * cellW);
                w = Math.min(w, sheet.getWidth() - x);
                int ch = Math.min(h, sheet.getHeight() - y);
                if (x >= 0 && y >= 0 && w > 0 && ch > 0) {
                    frames[b][f] = sheet.getSubimage(x, y, w, ch);
                }
            }
        }
        return frames;
    }

    /**
     * Returns a single animation frame for a flying bird. The frame index is wrapped automatically.
     * Called every render tick by {@link BirdSprite#getImage(BirdSpriteSheet)} when the sprite is FLYING.
     *
     * @param direction flight-direction row: 0=diagonal, 1=upward, 2=horizontal
     * @param birdType  bird colour: 0=blue, 1=green, 2=red
     * @param frame     animation frame index (automatically wrapped)
     * @return the sub-image, or null if load() has not been called or failed
     */
    public BufferedImage getFlying(int direction, int birdType, int frame) {
        if (flying == null) return null;
        return flying[direction][birdType][frame % FLYING_FRAMES];
    }

    /**
     * Returns the struck (hit) frame for the given bird type.
     * Called by {@link BirdSprite#getImage(BirdSpriteSheet)} when the sprite is STRUCK.
     *
     * @param birdType bird colour: 0=blue, 1=green, 2=red
     * @return the hit-frame sub-image, or null if load() failed
     */
    public BufferedImage getStruck(int birdType) {
        if (struck == null) return null;
        return struck[birdType];
    }

    /**
     * Returns the falling frame for the given bird type.
     * Called by {@link BirdSprite#getImage(BirdSpriteSheet)} when the sprite is FALLING.
     *
     * @param birdType bird colour: 0=blue, 1=green, 2=red
     * @return the falling-frame sub-image, or null if load() failed
     */
    public BufferedImage getFalling(int birdType) {
        if (falling == null) return null;
        return falling[birdType];
    }

}
