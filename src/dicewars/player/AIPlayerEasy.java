package dicewars.player;

import java.awt.Color;
import java.util.ArrayList;

import dicewars.map.GameMap;
import dicewars.map.Tile;

/**
 * This is the most basic AI opponent. It always attacks if it can.
 */
public class AIPlayerEasy extends AIPlayer {
    public AIPlayerEasy(Color color) {
        super(color);
    }

    @Override
    public PlayerAction tick(GameMap gameMap) {
        for (int x = 0; x < gameMap.COLUMNS; x++) {
            for (int y = 0; y < gameMap.ROWS; y++) {
                if (gameMap.getTile(x, y).getOwner() == this) {
                    ArrayList<Tile> neighbours = gameMap.getNeighbours(gameMap.getTile(x, y));
                    for (Tile t : neighbours) {
                        if (t.getOwner() != this && (gameMap.getTile(x, y).getDices() > 1)) {
                            return new PlayerAction(gameMap.getTile(x, y), t, false);
                        }
                    }
                }
            }
        }
        return new PlayerAction(null, null, true);
    }
}
