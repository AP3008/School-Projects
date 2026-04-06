package main.gameplay;
import main.modes.Difficulty;

/**
* Represents a single word used in gameplay.
*
* Each word has a text value and an associated difficulty level.
* Words are loaded from CSV files by {@link WordRepository} and
* used to create {@link WordTarget} objects during a game session.
*
* @author Garv Sharma
* 
* @see WordRepository
* @see WordTarget
*/

public class Word{

    /** The text that the player must type to complete this word. */
    private String text;

    

   /**
    * Constructs a Word with the given text and difficulty.
    *
    * @param text       the word string
    * @param difficulty the difficulty level of this word
    */

    public Word(String text, Difficulty difficulty) {
        this.text = text;
    }

    /**
     * Returns the text of this word.
     *
     * Called by {@link WordTarget} to determine typing progress.
     *
     * @return the non-null text string the player must type
     */
    public String getText() {
        return text;
    }
}