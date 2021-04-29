package dicewars.player;

import java.awt.Color;
import java.util.List;

import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.state.events.PlayerAction;

/**
 * This is the defensive AI. It scores the tiles not only by the number of dices,
 * but it is less likely to attack using tiles that are surrounded by more enemy tiles.
 */
public class AIPlayerDefensive extends AIPlayer {

    public AIPlayerDefensive(Color color) {
        super(color);
    }

    private int calculateDanger(List<Tile> tiles, int n) {
        int res = 0;
        for (Tile t : tiles) {
            if (t.getOwner() != this) res += (n - t.getDices()) / 8.0;
        }
        return res;
    }

    @Override
    public PlayerAction tick(GameMap gameMap) {
        List<Tile> tiles = gameMap.getTiles(this);
        Tile bestOptionOwn = null, bestOptionOpp = null;
        double bestOptionScore = -1;
        for (Tile cOwn : tiles) {
            List<Tile> neighbours = gameMap.getNeighbours(cOwn);
            for (Tile cOpp : neighbours) {
                if (cOpp.getOwner() != this && cOwn.getDices() > 1) {
                    double cScore =
                        cOwn.getDices() - cOpp.getDices() +
                        (cOwn.getDices() == 8 ? 0.5 : 0) -
                        calculateDanger(gameMap.getNeighbours(cOwn), cOwn.getDices()) / 5.0;
                    if (cScore > bestOptionScore) {
                        bestOptionOwn = cOwn;
                        bestOptionOpp = cOpp;
                        bestOptionScore = cScore;
                    }
                }
            }
        }
        if (bestOptionScore > 0)
            return new PlayerAction(bestOptionOwn, bestOptionOpp, false);
        return new PlayerAction(null, null, true);
    }
    
}
