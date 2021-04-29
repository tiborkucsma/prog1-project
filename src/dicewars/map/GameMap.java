package dicewars.map;

import dicewars.player.Player;
import dicewars.state.events.DiceDistribution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Describes the game map.
 */
public class GameMap implements Serializable {
    private final Tile[][] map;
    public final int COLUMNS;
    public final int ROWS;

    /**
     * Generate a new map for the given players
     * @param players players
     */
    public GameMap(List<Player> players) {
        int tilePerPlayer = 6;
        ROWS = (int) Math.ceil(Math.sqrt(((tilePerPlayer + 6) / 2) * players.size()));
        COLUMNS = 2 * ROWS;
        map = new Tile[COLUMNS][ROWS];
        for (int x = 0; x < COLUMNS; x++)
            for (int y = 0; y < ROWS; y++)
                map[x][y] = new Tile(x, y);

        Random rand = new Random();
        int x = rand.nextInt(COLUMNS - 1), y = rand.nextInt(ROWS - 1);
        int nonNeutral = 1;
        map[x][y].neutral = false;
        while (nonNeutral != players.size() * tilePerPlayer) {
            x = rand.nextInt(COLUMNS - 1);
            y = rand.nextInt(ROWS - 1);
            if (noOfNeighbours(x, y) > 0 && map[x][y].neutral) {
                map[x][y].neutral = false;
                nonNeutral++;
            }
        }

        for (int i = 0; i < players.size(); i++) {
            int n = 0;
            while (n < tilePerPlayer) {
                x = rand.nextInt(COLUMNS - 1);
                y = rand.nextInt(ROWS - 1);
                if (!map[x][y].neutral && map[x][y].getOwner() == null) {
                    map[x][y].setOwner(players.get(i));
                    map[x][y].setDices(1);
                    n++;
                }
            }

            n = 3 * tilePerPlayer - tilePerPlayer;
            distributeDices(getTiles(players.get(i)), n);
        }
    }

    /**
     * Create a copy of a map.
     * @param gameMap Map to copy
     */
    public GameMap(GameMap gameMap) {
        this.COLUMNS = gameMap.COLUMNS;
        this.ROWS = gameMap.ROWS;
        this.map = new Tile[this.COLUMNS][this.ROWS];
        for (int x = 0; x < COLUMNS; x++)
            for (int y = 0; y < ROWS; y++)
                this.map[x][y] = new Tile(gameMap.map[x][y]);
    }

    /**
     * Counts the dices on a set of tiles
     * @param tiles Target tiles
     * @return count of dices
     */
    public int countDices(List<Tile> tiles) {
        if (tiles == null) return 0;
        int res = 0;
        for (Tile tile : tiles) {
            res += tile.getDices();
        }
        return res;
    }

    /**
     * Distributes n dices randomly between the given tiles.
     * @param tiles Tiles to distribute the dices between
     * @param n Number of dices to distribute
     */
    public DiceDistribution distributeDices(List<Tile> tiles, int n) {
        if (tiles.size() == 0) return null;
        DiceDistribution dd = new DiceDistribution();
        Random rand = new Random();
        while (n > 0) {
            int i = rand.nextInt(tiles.size());
            if (tiles.get(i).incDices() == 1) {
                dd.addDices(tiles.get(i), 1);
                n--;
            }
            if (countDices(tiles) == tiles.size() * 8) break; // stop the loop if all the tiles are full
        }
        return dd;
    }

    /**
     * Checks if a coordinate is in the bounds of this map.
     * @param q x coordinate
     * @param r y coordinate
     * @return true if the map contains the coordinate false otherwise
     */
    public boolean inBounds(int q, int r) {
        return  r >= 0 &&
                q >= 0 &&
                r < ROWS &&
                q < COLUMNS;
    }

    /**
     * Get a tile with the given coords
     * @param q x coordinate
     * @param r y coordinate
     * @return the tile or null if coords are outside the map
     */
    public Tile getTile(int q, int r) {
        return inBounds(q, r) ? map[q][r] : null;
    }

    /**
     * Get the tiles of a given player
     * @param owner The player
     * @return A list of their tiles
     */
    public ArrayList<Tile> getTiles(Player owner) {
        ArrayList<Tile> res = new ArrayList<>();
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if (map[x][y].getOwner() == owner) {
                    res.add(map[x][y]);
                }
            }
        }
        return res;
    }

    /**
     * Check if two tiles are adjacent
     * @param t1 tile 1
     * @param t2 tile 2
     * @return true if they are adjacent false otherwise
     */
    public static boolean adjacent(Tile t1, Tile t2) {
        if (Math.abs(t1.X - t2.X) <= 1 && Math.abs(t1.Y - t2.Y) <= 1) {
            if (t1.Y % 2 == 1)
                return t1.X <= t2.X || t1.Y == t2.Y; // (t1.X > t2.X) -> (t1.Y == t2.Y) if row is odd
            else
                return t1.X >= t2.X || t1.Y == t2.Y; // (t1.x < t2.X) -> (t1.Y == t2.Y) if row is even
        }
        return false;
    }

    /**
     * Get the neighbours of a tile
     * @param t tile
     * @return List of neighbours
     */
    public ArrayList<Tile> getNeighbours(Tile t) {
        return getNeighbours(t.X, t.Y);
    }

    /**
     * Get the neighbours of a tile
     * @param q x coord of tile
     * @param r y coord of tile
     * @return List of neighbours
     */
    public ArrayList<Tile> getNeighbours(int q, int r) {
        ArrayList<Tile> res = new ArrayList<>();
        boolean isOddRow = r % 2 == 1;
        for (int x = (isOddRow ? 0 : -1); x <= (isOddRow ? 1 : 0); x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                if (!(x == 0 && y == 0) &&
                        inBounds(q + x, r + y) &&
                        !map[q + x][r + y].neutral)
                {
                    res.add(map[q + x][r + y]);
                }
            }
        }
        if (isOddRow && inBounds(q - 1, r) && !map[q-1][r].neutral) res.add(map[q - 1][r]);
        else if (!isOddRow && inBounds(q + 1, r) && !map[q+1][r].neutral) res.add(map[q + 1][r]);
        return res;
    }

    /**
     * Count the neighbours of a tile
     * @param q x coord of tile
     * @param r y coord of tile
     * @return count of neighbours
     */
    public int noOfNeighbours(int q, int r) {
        int n = 0;
        boolean isOddRow = r % 2 == 1;
        for (int x = (isOddRow ? 0 : -1); x <= (isOddRow ? 1 : 0); x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                if (!(x == 0 && y == 0) &&
                    inBounds(q + x, r + y) &&
                    !map[q + x][r + y].neutral)
                {
                    n++;
                }
            }
        }
        if (isOddRow && inBounds(q - 1, r) && !map[q-1][r].neutral) n++;
        else if (!isOddRow && inBounds(q + 1, r) && !map[q+1][r].neutral) n++;
        return n;
    }

    /**
     * Generate string image of map (only used for development)
     * @return Generated string
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int r = 0; r < ROWS; r++)
        {
            if (r % 2 == 1) res.append("      ");
            for (int q = 0; q < COLUMNS; q++)
            {
                if (map[q][r].neutral)
                    res.append("(").append(q).append(",").append(r).append(",-)     ");
                else
                    res.append("(").append(q).append(",").append(r).append(",").append(noOfNeighbours(q, r)).append(")     ");
            }
            res.append(System.lineSeparator());
        }
        return res.toString();
    }
}
