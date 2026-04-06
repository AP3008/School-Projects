package main.ui;

/**
 * Interface for screens that need to reload data each time they become visible.
 *
 * Implemented by LevelSelectScreen, PlayerStatsScreen, HighScoreScreen,
 * ViewPlayerStatsParentalScreen, and PlayerScreen. Called by {@link ScreenManager#showScreen(String)}.
 *
 * Artificial Intelligence Tool: Gemini; Methodology: The idea for this interface came from trying to debug during a conversation with an LLM;
 * @author Adam Porbanderwalla
 */
public interface Refreshable {

    /**
     * Refreshes the screen's displayed data. Called by {@link ScreenManager#showScreen(String)}
     * whenever this screen is brought to the front.
     */
    void onScreenShown();
}
