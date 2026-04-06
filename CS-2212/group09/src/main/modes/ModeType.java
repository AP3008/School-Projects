package main.modes;

/**
* Represents the available game modes.
* 
* Each mode defines a different gameplay experience for the player.
*
* @author Garv Sharma
* @see NormalMode
* @see EndlessMode
* @see TimedMode
*/

public enum ModeType {

    /**
     * A ten-level progression mode.  Word difficulty and spawn rate increase with each level.
     */
    NORMAL,

    /**
     * A continuous survival mode with no fixed endpoint.  The session continues until the player loses all lives.  
     */
    ENDLESS,

    /**
     * A fixed-duration (60-second) mode.  Missing a word carries no life penalty; the session ends when the timer runs out.
     */
    TIMED
}