package main.gameplay;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.Difficulty;

// NOTE: WordRepository loads CSV files at relative paths like "src/main/gameplay/easy.csv".
// These tests must be run from the project root directory (group09/).
class WordRepositoryTest {

    @Test
    void constructor_loadsWordsFromCsvFiles() {
        WordRepository repo = new WordRepository();
        // If CSVs loaded correctly, we should get non-null words for each difficulty
        assertNotNull(repo.getRandomWord(Difficulty.EASY));
        assertNotNull(repo.getRandomWord(Difficulty.MEDIUM));
        assertNotNull(repo.getRandomWord(Difficulty.HARD));
    }

    @Test
    void getRandomWord_returnedWordHasNonEmptyText() {
        WordRepository repo = new WordRepository();

        Word word = repo.getRandomWord(Difficulty.EASY);

        assertNotNull(word.getText());
        assertFalse(word.getText().isEmpty());
    }
}
