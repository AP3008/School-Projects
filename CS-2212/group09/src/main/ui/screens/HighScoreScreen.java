package main.ui.screens;

import main.modes.ModeType;
import main.persistence.HighScoreTable;
import main.ui.Refreshable;
import main.ui.ScreenManager;
import main.ui.components.BackgroundPanel;
import main.ui.components.ImageButton;
import main.ui.components.MenuKeyHandler;
import main.ui.components.PixelLabel;
import main.ui.components.StyledButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Screen displaying top-10 high scores per game mode (Normal, Timed, Endless).
 * Three tab buttons switch the active mode and trigger a score refresh from HighScoreTable.
 * Empty slots show "---". Back button and Escape return to {@link MainMenuScreen}.
 * Implements {@link main.ui.Refreshable} to refresh scores on each visit. Shown by {@link ScreenManager}.
 *
 * @author Jaideep Singh, Rahul
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html">javax.swing</a>
 */
public class HighScoreScreen extends BackgroundPanel implements Refreshable {

    private ScreenManager screenManager;
    private JPanel scoresPanel;
    private ModeType currentMode = ModeType.NORMAL;
    private StyledButton normalBtn;
    private StyledButton timedBtn;
    private StyledButton endlessBtn;
    private MenuKeyHandler keyHandler;

    /**
     * Constructs the high-score screen with a title, three mode tab buttons, a scores area,
     * and a Back button. Attaches action listeners to tab buttons, sets up keyboard navigation,
     * and registers the Escape key shortcut.
     *
     * @param screenManager the application-wide {@link ScreenManager} used to switch screens
     */
    public HighScoreScreen(ScreenManager screenManager) {
        super("bg/generic-menu-bg.png");
        this.screenManager = screenManager;
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

        PixelLabel titleLabel = new PixelLabel("High Scores", 48f, new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        tabPanel.setOpaque(false);
        normalBtn = new StyledButton("Normal", 150, 45);
        timedBtn = new StyledButton("Timed", 150, 45);
        endlessBtn = new StyledButton("Endless", 150, 45);
        tabPanel.add(normalBtn);
        tabPanel.add(timedBtn);
        tabPanel.add(endlessBtn);
        tabPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tabPanel.setMaximumSize(new Dimension(500, 50));

        normalBtn.addActionListener(e -> { currentMode = ModeType.NORMAL; refreshScores(); });
        timedBtn.addActionListener(e -> { currentMode = ModeType.TIMED; refreshScores(); });
        endlessBtn.addActionListener(e -> { currentMode = ModeType.ENDLESS; refreshScores(); });

        scoresPanel = new JPanel();
        scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
        scoresPanel.setOpaque(false);
        scoresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel scoresWrapper = new JPanel(new BorderLayout());
        scoresWrapper.setOpaque(false);
        scoresWrapper.setMaximumSize(new Dimension(550, 350));
        scoresWrapper.setPreferredSize(new Dimension(550, 350));
        scoresWrapper.add(scoresPanel, BorderLayout.NORTH);
        scoresWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton backBtn = new ImageButton("back-button");
        backBtn.setMaximumSize(new Dimension(250, 55));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> screenManager.showScreen(ScreenManager.MAIN_MENU));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(tabPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(scoresWrapper);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(backBtn);

        add(contentPanel);

        keyHandler = new MenuKeyHandler(Arrays.asList(normalBtn, timedBtn, endlessBtn, backBtn));
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
     * Called by {@link ScreenManager} when this screen becomes visible.
     * Refreshes the scores list for the current mode and resets keyboard focus.
     */
    @Override
    public void onScreenShown() {
        refreshScores();
        keyHandler.resetFocus();
    }

    /**
     * Clears and repopulates the scores panel with a header and up to 10 score rows for the current mode.
     * Retrieves data from HighScoreTable via ScreenManager. Empty slots display "---".
     * Alternates row colours between white and light grey. Revalidates and repaints after population.
     */
    private void refreshScores() {
        scoresPanel.removeAll();

        JPanel headerRow = createRow("#", "Player", "Score", new Color(255, 215, 0));
        scoresPanel.add(headerRow);
        scoresPanel.add(Box.createVerticalStrut(5));

        HighScoreTable table = screenManager.getHighScoreTable();
        var entries = table.getTop(currentMode, 10);

        for (int i = 0; i < 10; i++) {
            String rank = String.valueOf(i + 1);
            String name = "---";
            String score = "---";
            if (i < entries.length && entries[i] != null) {
                name = entries[i].getUsername();
                score = String.valueOf(entries[i].getScore());
            }
            Color rowColor = (i % 2 == 0) ? new Color(255, 255, 255) : new Color(200, 200, 200);
            JPanel row = createRow(rank, name, score, rowColor);
            scoresPanel.add(row);
        }

        scoresPanel.revalidate();
        scoresPanel.repaint();
    }

    /**
     * Creates a single score row with three PixelLabels for rank, player name, and score.
     * Used for both the header row and data rows in refreshScores().
     *
     * @param rank      rank number or column heading
     * @param name      player username or column heading
     * @param score     score value or column heading
     * @param textColor colour applied to all three labels
     * @return a transparent JPanel containing the three labels in a 1x3 grid
     */
    private JPanel createRow(String rank, String name, String score, Color textColor) {
        JPanel row = new JPanel(new GridLayout(1, 3));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(550, 30));

        PixelLabel rankLabel = new PixelLabel(rank, 22f, textColor);
        PixelLabel nameLabel = new PixelLabel(name, 22f, textColor);
        PixelLabel scoreLabel = new PixelLabel(score, 22f, textColor);

        row.add(rankLabel);
        row.add(nameLabel);
        row.add(scoreLabel);
        return row;
    }
}
