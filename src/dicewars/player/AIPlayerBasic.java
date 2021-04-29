package dicewars.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.state.events.PlayerAction;

/**
 * This is the most basic AI opponent. It always attacks if it can, and always selects the first valid move.
 */
public class AIPlayerBasic extends AIPlayer {
    public AIPlayerBasic(Color color) {
        super(color);
    }

    @Override
    public PlayerAction tick(GameMap gameMap) {
        List<Tile> tiles = gameMap.getTiles(this);
        for (Tile cOwn : tiles) {
            List<Tile> neighbours = gameMap.getNeighbours(cOwn);
            for (Tile cOpp : neighbours) {
                if (cOpp.getOwner() != this && cOwn.getDices() > 1) {
                    return new PlayerAction(cOwn, cOpp, false);
                }
            }
        }
        return new PlayerAction(null, null, true);
    }
}
