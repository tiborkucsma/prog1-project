package dicewars.player;

import dicewars.map.GameMap;
import dicewars.map.Tile;

import java.awt.*;
import java.util.ArrayList;

public class AIPlayer extends Player {

    public AIPlayer(Color color) {
        super(color);
    }

    public PlayerAction tick(GameMap gameMap) {
        for (int x = 0; x < gameMap.COLUMNS; x++) {
            for (int y = 0; y < gameMap.ROWS; y++) {
                if (gameMap.getTile(x, y).owner == this) {
                    ArrayList<Tile> neighbours = gameMap.getNeighbours(gameMap.getTile(x, y));
                    for (Tile t : neighbours) {
                        if (t.owner != this && (t.dices < gameMap.getTile(x, y).dices - 2 || gameMap.getTile(x, y).dices == 8)) {
                            return new PlayerAction(gameMap.getTile(x, y), t, false);
                        }
                    }
                }
            }
        }
        return new PlayerAction(null, null, true);
    }
}
