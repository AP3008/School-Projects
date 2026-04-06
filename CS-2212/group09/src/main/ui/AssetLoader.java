package main.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

/**
 * Static utility for loading and caching image and font assets used across the KeyHunter UI.
 *
 * Images are stored in a ConcurrentHashMap so each file is read from disk only once.
 * Used by virtually every screen and component in the ui package, and by {@link BirdSpriteSheet}.
 *
 * @author Adam Porbanderwalla, Garv Sharma
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html">ConcurrentHashMap</a>
 */
public class AssetLoader {

    /** Thread-safe image cache keyed by relative asset path. */
    private static final Map<String, BufferedImage> imageCache = new ConcurrentHashMap<>();

    /** Root directory for all asset files relative to the project working directory. */
    private static final String ASSET_BASE = "src/main/assets/";

    private static Font pixelFont = null;

    /**
     * Loads a BufferedImage from the assets folder, returning a cached copy on subsequent calls.
     * Used by all screens and {@link BirdSpriteSheet} to load backgrounds, buttons, and sprites.
     *
     * @param relativePath path relative to src/main/assets/
     * @return the decoded image, or null if loading failed
     */
    public static BufferedImage loadImage(String relativePath) {
        if (imageCache.containsKey(relativePath)) {
            return imageCache.get(relativePath);
        }
        try {
            File file = new File(ASSET_BASE + relativePath);
            BufferedImage img = ImageIO.read(file);
            imageCache.put(relativePath, img);
            return img;
        } catch (Exception e) {
            System.err.println("Failed to load image: " + relativePath + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the Jersey10-Regular pixel font at the requested size.
     * Falls back to Monospaced Bold if the font file is missing.
     * Used by all screens that render styled text.
     *
     * @param size desired font size in points
     * @return the Font instance at the requested size
     */
    public static Font getPixelFont(float size) {
        if (pixelFont == null) {
            try {
                File fontFile = new File(ASSET_BASE + "font/Jersey10-Regular.ttf");
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFont);
            } catch (Exception e) {
                System.err.println("Failed to load pixel font: " + e.getMessage());
                pixelFont = new Font("Monospaced", Font.BOLD, 12);
            }
        }
        return pixelFont.deriveFont(size);
    }

    /**
     * Returns a bilinear-scaled copy of the given image at the specified dimensions.
     * Used by screen classes and {@link BirdSpriteSheet} to resize assets to fit UI slots.
     *
     * @param img    the source image; returns null if null
     * @param width  desired width in pixels
     * @param height desired height in pixels
     * @return a new BufferedImage at the requested size, or null if img was null
     */
    public static BufferedImage scaleImage(BufferedImage img, int width, int height) {
        if (img == null) return null;
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, width, height, null);
        g2.dispose();
        return scaled;
    }
}
