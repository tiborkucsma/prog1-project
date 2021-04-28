package dicewars.player;

import java.awt.Color;
import java.util.List;

import dicewars.map.GameMap;
import dicewars.map.Tile;

public class AIPlayerHard extends AIPlayer {

    public AIPlayerHard(Color color) {
        super(color);
    }

    private int countOnes(List<Tile> ts) {
        int n = 0;
        for (Tile t : ts) {
            if (t.getDices() == 1) n++;
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
                if (cOpp.getOwner() != this && cOwn.getDices() > cOpp.getDices()) {
                    double cScore =
                        Math.exp(cOwn.getDices() - cOpp.getDices()) +
                        (cOwn.getDices() == 8 ? 0.5 : 0) +
                        Math.exp(gameMap.getTiles(cOpp.getOwner()).size() - tiles.size()) / 1.5;
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
