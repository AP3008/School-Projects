package main.persistence;

import java.util.ArrayList;
import java.util.List;
import main.modes.ModeType;

/**
 * Stores the best score for every player across each game mode.
 * Only one entry per player per mode is kept — if a new score is higher,
 * it replaces the old one. Lower scores are ignored.
 *
 * @author Rahul
 */
public class HighScoreTable {

    // The list of all best-score entries across all players and modes
    private List<HighScoreEntry> entries;

    /**
     * Creates an empty HighScoreTable.
     */
    public HighScoreTable() {
        this.entries = new ArrayList<>();
    }

    /**
     * Records a score entry for a given mode.
     * If the player already has an entry for this mode, it is only replaced
     * if the new score is higher. Otherwise the entry is added.
     *
     * @param mode  the game mode the entry belongs to
     * @param entry the HighScoreEntry to record
     */
    public void record(ModeType mode, HighScoreEntry entry) {
        // Only keep one entry per player per mode — replace if new score is higher
        for (int i = 0; i < entries.size(); i++) {
            HighScoreEntry existing = entries.get(i);
            if (existing.getModeType() == mode
                    && existing.getUsername().equals(entry.getUsername())) {
                if (entry.getScore() > existing.getScore()) {
                    entries.set(i, entry);
                }
                return;
            }
        }
        this.entries.add(entry);
    }

    /**
     * Returns the top-scoring entries for a given game mode, sorted highest to lowest.
     * Returns at most the number specified by limit.
     *
     * @param mode  the game mode to filter by
     * @param limit the maximum number of entries to return
     * @return an array of top HighScoreEntry objects, may be empty
     */
    public HighScoreEntry[] getTop(ModeType mode, int limit) {
        // Step 1: Create a temporary list and add only the scores for the requested mode
        ArrayList<HighScoreEntry> matchingScores = new ArrayList<>();

        for (HighScoreEntry entry : this.entries) {
            if (entry.getModeType() == mode) {
                matchingScores.add(entry);
            }
        }

        // Step 2: Sort those matching scores from highest to lowest
        for (int i = 0; i < matchingScores.size(); i++) {
            for (int j = i + 1; j < matchingScores.size(); j++) {

                // Grab the two scores we are comparing
                int leftScore = matchingScores.get(i).getScore();
                int rightScore = matchingScores.get(j).getScore();

                // If the score on the right is BIGGER than the score on the left...
                if (rightScore > leftScore) {

                    // ... swap their positions in the list!
                    HighScoreEntry temp = matchingScores.get(i);
                    matchingScores.set(i, matchingScores.get(j));
                    matchingScores.set(j, temp);
                }
            }
        }

        // Step 3: Figure out how many scores to actually return
        int actualSize = Math.min(limit, matchingScores.size());

        // Step 4: Create the final array and copy the top scores into it
        HighScoreEntry[] topScores = new HighScoreEntry[actualSize];

        for (int i = 0; i < actualSize; i++) {
            topScores[i] = matchingScores.get(i);
        }

        return topScores;
    }

    /**
     * Returns a copy of all entries in the table regardless of mode.
     * Used when saving the table to disk.
     *
     * @return a list of all HighScoreEntry objects in the table
     */
    public List<HighScoreEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }

    /**
     * Clears all entries from the table, wiping the entire leaderboard.
     */
    public void reset() {
        this.entries.clear();
    }

    /**
     * Removes all entries belonging to the specified player.
     * Used when a player account is deleted.
     *
     * @param username the username whose entries should be removed
     */
    public void resetForPlayer(String username) {
        entries.removeIf(e -> e.getUsername().equals(username));
    }
}
