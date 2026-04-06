package main.ui.components;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * KeyAdapter that provides keyboard navigation between an ordered list of focusable components.
 * DOWN/RIGHT moves focus forward, UP/LEFT moves it backward (both wrap around), and ENTER clicks
 * the focused button. Used in MainMenuScreen and PlayerScreen for full keyboard-only navigation.
 *
 * @author Imad Tahir
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class MenuKeyHandler extends KeyAdapter {

    private final List<JComponent> navigables;
    private int focusIndex = 0;
    private JComponent installedContainer;

    /**
     * Constructs a MenuKeyHandler for the given ordered list of navigable components.
     * Call install() afterwards to attach the key listeners.
     *
     * @param navigables the ordered list of components to cycle focus between
     */
    public MenuKeyHandler(List<JComponent> navigables) {
        this.navigables = navigables;
    }

    /**
     * Installs this handler on the container and all navigable components, then moves focus to the first item.
     * Called by MainMenuScreen and PlayerScreen when setting up keyboard navigation.
     *
     * @param container the root panel to also attach the key listener to
     */
    public void install(JComponent container) {
        container.setFocusable(true);
        container.addKeyListener(this);
        installedContainer = container;
        for (JComponent c : navigables) {
            c.addKeyListener(this);
        }
        resetFocus();
    }

    /**
     * Removes this handler from the container and all navigable components.
     * Called when the screen is hidden or navigation is no longer needed.
     */
    public void uninstall() {
        if (installedContainer != null) {
            installedContainer.removeKeyListener(this);
        }
        for (JComponent c : navigables) {
            c.removeKeyListener(this);
        }
        installedContainer = null;
    }

    /**
     * Resets focus to the first navigable component. Called by install() and by screens on show.
     */
    public void resetFocus() {
        focusIndex = 0;
        if (!navigables.isEmpty()) {
            SwingUtilities.invokeLater(() -> navigables.get(0).requestFocusInWindow());
        }
    }

    /**
     * Handles arrow-key navigation and ENTER activation. All handled events are consumed.
     * Called by Swing when a key is pressed on any component this handler is registered to.
     *
     * @param e the KeyEvent from the registered component or container
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (navigables.isEmpty()) return;
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_RIGHT) {
            focusIndex = (focusIndex + 1) % navigables.size();
            navigables.get(focusIndex).requestFocusInWindow();
            e.consume();
        } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_LEFT) {
            focusIndex = (focusIndex - 1 + navigables.size()) % navigables.size();
            navigables.get(focusIndex).requestFocusInWindow();
            e.consume();
        } else if (code == KeyEvent.VK_ENTER) {
            JComponent focused = navigables.get(focusIndex);
            if (focused instanceof AbstractButton) {
                ((AbstractButton) focused).doClick();
                e.consume();
            }
        }
    }
}
