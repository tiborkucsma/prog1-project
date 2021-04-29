package dicewars.state.events;

import java.io.Serializable;

import dicewars.map.GameMap;

/**
 * Super class of all classes that describe events that happen over a game. These are used in replays.
 */
public abstract class GameEvent implements Serializable {
    /**
     * Translates the event to new map. (Fixes references)
     * @param map New map
     * @return New event with correct references for the new map
     */
    public abstract GameEvent translateToMap(GameMap map);
}
