import java.util.ArrayList;

public class AIPlayer extends Player {
    public int tick(GameMap gameMap) {
        for (int x = 0; x < gameMap.COLUMNS; x++) {
            for (int y = 0; y < gameMap.ROWS; y++) {
                if (gameMap.getTile(x, y).owner == this) {
                    ArrayList<Tile> neighbours = gameMap.getNeighbours(gameMap.getTile(x, y));
                    for (Tile t : neighbours) {
                        if (t.owner != this && t.dices < gameMap.getTile(x, y).dices) {
                            attack(gameMap.getTile(x, y), t);
                            return 1;
                        }
                    }
                }
            }
        }
        return -1;
    }
}
