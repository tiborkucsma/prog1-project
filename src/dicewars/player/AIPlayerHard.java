package dicewars.player;

import java.awt.Color;
import java.util.List;

import dicewars.map.GameMap;
import dicewars.map.Tile;

public class AIPlayerHard extends AIPlayer {

    public AIPlayerHard(Color color) {
        super(color);
    }

    private int countOpponents(List<Tile> ts) {
        int n = 0;
        for (Tile t : ts) {
            if (t.owner != this) n++;
        }
        return n;
    }

    @Override
    public PlayerAction tick(GameMap gameMap) {
        List<Tile> tiles = gameMap.getTiles(this);
        Tile bestOptionOwn = null, bestOptionOpp = null;
        double bestOptionScore = -1;
        for (Tile cOwn : tiles) {
            List<Tile> neighbours = gameMap.getNeighbours(cOwn);
            for (Tile cOpp : neighbours) {
                if (cOpp.owner != this && cOwn.dices > 1) {
                    double cScore =
                        (cOwn.dices - cOpp.dices) +
                        (cOwn.dices == 8 ? 5.0 : 0) +
                        (6 - countOpponents(gameMap.getNeighbours(cOpp))) / 4.0;
                    if (cScore > bestOptionScore) {
                        bestOptionOwn = cOwn;
                        bestOptionOpp = cOpp;
                        bestOptionScore = cScore;
                    }
                }
            }
        }
        if (bestOptionScore > 1) return new PlayerAction(bestOptionOwn, bestOptionOpp, false);
        return new PlayerAction(null, null, true);
    }
    
}
