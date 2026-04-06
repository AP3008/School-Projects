package main.engine;

/**
 * Represents the possible states of an active game session in KeyHunter.
 *
 * Used by {@link GameEngine} and {@link GameSession} to control the flow of gameplay.
 * {@link GameEngine#pause()} transitions RUNNING to PAUSED, {@link GameEngine#resume()} reverses it,
 * and {@link GameEngine#endSession()} transitions to GAME_OVER.
 * GameScreen reads the state each tick to decide whether to advance the game loop.
 *
 * @author Adam Porbanderwalla
 */
public enum GameState{
    RUNNING,
    PAUSED,
    GAME_OVER
}