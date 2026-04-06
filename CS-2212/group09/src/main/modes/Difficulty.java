package main.modes;

/**
* Represents the difficulty level of a word or game element.
* 
* Difficulty levels are used to categorize words and control gameplay progression.
*
* @author Garv Sharma
*/

public enum Difficulty {

    /**
     * Easy difficulty.  Short, common words suitable for the early stages of a session.
     */
    EASY,

    /**
     * Medium difficulty.  Words of moderate length and frequency.  The predominant difficulty tier in
     */
    MEDIUM,

    /**
     * Long or uncommon words that challenge experienced players.  In {@link NormalMode} this
     */
    HARD
}