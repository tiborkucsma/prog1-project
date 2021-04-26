package dicewars.player;

import java.awt.Color;
import java.util.ArrayList;

import dicewars.map.GameMap;
import dicewars.map.Tile;

/**
 * This is the medium difficulty AI opponent.
 */
public class AIPlayerMedium extends AIPlayer {

    public AIPlayerMedium(Color color) {
        super(color);
    }

    @Override
    public PlayerAction tick(GameMap gameMap) {
        for (int x = 0; x < gameMap.COLUMNS; x++) {
            for (int y = 0; y < gameMap.ROWS; y++) {
                if (gameMap.getTile(x, y).owner == this) {
                    Tile cOwn = gameMap.getTile(x, y);
                    ArrayList<Tile> neighbours = gameMap.getNeighbours(cOwn);
                    Tile best_option = null;
                    for (Tile t : neighbours) {
                        if (t.owner != this && (t.dices < cOwn.dices || cOwn.dices == 8)) {
                            if (best_option == null || best_option.dices < t.dices) {
                                best_option = t;
                            }
                        }
                    }
                    if (best_option != null) return new PlayerAction(gameMap.getTile(x, y), best_option, false);
                }
            }
        }
        return new PlayerAction(null, null, true);
    }

}
