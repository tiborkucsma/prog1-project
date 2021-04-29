package dicewars.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import dicewars.map.GameMap;
import dicewars.map.Tile;

/**
 * This is the offensive AI. It only attacks if it has more than 3 dices on a tile
 * and it scores actions based on the number of dices on it's own and the enemy's
 * tile and selects the highes score.
 */
public class AIPlayerOffensive extends AIPlayer {

    public AIPlayerOffensive(Color color) {
        super(color);
    }

    @Override
    public PlayerAction tick(GameMap gameMap) {
        List<Tile> tiles = gameMap.getTiles(this);
        Tile bestOptionOwn = null, bestOptionOpp = null;
        double bestOptionScore = -1;
        for (Tile cOwn : tiles) {
            List<Tile> neighbours = gameMap.getNeighbours(cOwn);
            for (Tile cOpp : neighbours) {
                if (cOpp.getOwner() != this && cOwn.getDices() > 3) {
                    double cScore = cOwn.getDices() - cOpp.getDices();
                    if (cScore > bestOptionScore) {
                        bestOptionOwn = cOwn;
                        bestOptionOpp = cOpp;
                        bestOptionScore = cScore;
                    }
                }
            }
        }
        if (bestOptionOwn != null && bestOptionOpp != null)
            return new PlayerAction(bestOptionOwn, bestOptionOpp, false);
        return new PlayerAction(null, null, true);
    }

}
