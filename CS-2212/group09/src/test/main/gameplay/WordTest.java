package main.gameplay;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.modes.Difficulty;

class WordTest {

    @Test
    void constructor_storesText() {
        Word word = new Word("hello", Difficulty.EASY);

        assertEquals("hello", word.getText());
    }

}
