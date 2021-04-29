package dicewars;

import java.util.HashMap;
import java.util.Map;

import dicewars.map.GameMap;
import dicewars.map.Tile;

/**
 * Describes a dice distribution at the end of a turn.
 */
public class DiceDistribution extends GameEvent {
    private Map<Tile, Integer> addedDices;

    /**
     * Default constructor. Initializes the dice map with a HashMap.
     */
    public DiceDistribution() {
        addedDices = new HashMap<>();
    }

    /**
     * Translates the event to new map. (Fixes references)
     * @param map New map
     * @return New event with correct references for the new map
     */
    @Override
    public GameEvent translateToMap(GameMap map) {
        DiceDistribution tEvent = new DiceDistribution();
        for (Map.Entry<Tile, Integer> entry : addedDices.entrySet()) {
            Tile tTile = map.getTile(entry.getKey().X, entry.getKey().Y);
            if (tTile == null) return null;
            tEvent.addDices(tTile, entry.getValue());
        }
        return tEvent;
    }

    /**
     * Executes this event.
     */
    public void execute() {
        for (Map.Entry<Tile, Integer> entry : addedDices.entrySet()) {
            entry.getKey().setDices(entry.getKey().getDices() + entry.getValue());
        }
    }

    /**
     * Add n dices to t tile.
     * @param t Target tile
     * @param n Number of dices
     */
    public void addDices(Tile t, int n) {
        if (t == null) return;
        int curr = addedDices.getOrDefault(t, 0);
        addedDices.put(t, curr + n);
    }

    /**
     * Get the number of dices added to a tile
     * @param t Target tile
     * @return Number of dices
     */
    public int getAddedDices(Tile t) {
        if (t == null) return 0;
        return addedDices.getOrDefault(t, 0);
    }
}
