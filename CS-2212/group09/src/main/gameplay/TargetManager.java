package main.gameplay;

import java.util.ArrayList;
import java.util.List;

import main.engine.GameEngine;

/**
* Manages all active word targets currently in the game.
* <p>
* The TargetManager stores the list of active {@link WordTarget} objects,
* updates them over time, and tracks how much time has passed since the most recent target was spawned.
*
* @author Garv Sharma
* @see WordTarget
* 
*/

public class TargetManager {

    /** The list of word targets currently active in the game. */
    private List<WordTarget> activeTargets;

    /** The time in seconds since the last target was spawned. */
    private double timeSinceLastSpawn;

    /**
    * Constructs a TargetManager with an empty list of active targets and a spawn timer set to 0.
    */

    public TargetManager() {
        this.activeTargets = new ArrayList<>();
        this.timeSinceLastSpawn = 0.0;
    }

    

   /**
    * Resets the manager by removing all active targets and resetting the spawn timer to 0.
    */

    public void reset() {
        activeTargets.clear();
        timeSinceLastSpawn = 0.0;
    }

    /**
     * Updates the spawn timer and all active targets.
     *
     * Called every frame by {@link GameEngine}. It updates the time since the last
     * spawn and tells each WordTarget to update based on the time passed.
     *
     * @param deltaSeconds time passed since the last frame (in seconds)
     */
    public void update(double deltaSeconds) {
        timeSinceLastSpawn += deltaSeconds;
        for (WordTarget target : activeTargets) {
            target.update(deltaSeconds);
        }
    }

    /**
    * Returns the amount of time since the last target was spawned.
    *
    * @return the time since the last spawn, in seconds
    */
    public double getTimeSinceLastSpawn() {
        return timeSinceLastSpawn;
    }

    /**
    * Adds a new target to the list of active targets and resets the
    * spawn timer.
    *
    * @param target the {@link WordTarget} to add
    */
    public void spawnTarget(WordTarget target) {
        activeTargets.add(target);
        timeSinceLastSpawn = 0.0;
    }

    /**
    * Removes a target from the list of active targets.
    *
    * @param target the {@link WordTarget} to remove
    */

    public void removeTarget(WordTarget target) {
        activeTargets.remove(target);
    }

    /**
     * Resets the spawn timer to zero without affecting the active-target list.
     */
    public void resetSpawnTimer() {
        timeSinceLastSpawn = 0.0;
    }

    /**
     * Removes all active targets without resetting the spawn timer..
     */
    public void clearAll() {
        activeTargets.clear();
    }

    /**
     * Returns the live list of active {@link WordTarget} objects.
     *
     * @return the mutable list of currently active targets
     */
    public List<WordTarget> getActiveTargets() {
        return activeTargets;
    }
}
