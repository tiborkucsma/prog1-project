package dicewars;

import java.util.HashMap;
import java.util.Map;

import dicewars.map.GameMap;
import dicewars.map.Tile;

public class EndTurnEvent extends GameEvent {
    private Map<Tile, Integer> addedDices;

    public EndTurnEvent() {
        addedDices = new HashMap<>();
    }

    public EndTurnEvent translateToMap(GameMap map) {
        EndTurnEvent tEvent = new EndTurnEvent();
        for (Map.Entry<Tile, Integer> entry : addedDices.entrySet()) {
            Tile tTile = map.getTile(entry.getKey().X, entry.getKey().Y);
            if (tTile == null) return null;
            tEvent.addDices(tTile, entry.getValue());
        }
        return tEvent;
    }

    public void execute() {
        for (Map.Entry<Tile, Integer> entry : addedDices.entrySet()) {
            entry.getKey().dices += entry.getValue();
        }
    }

    public void addDices(Tile t, int n) {
        if (t == null) return;
        int curr = addedDices.getOrDefault(t, 0);
        addedDices.put(t, curr + n);
    }

    public int getAddedDices(Tile t) {
        if (t == null) return 0;
        return addedDices.getOrDefault(t, 0);
    }
}
