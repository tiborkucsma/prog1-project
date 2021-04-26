package dicewars.map;

import dicewars.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class GameMap implements Serializable {
    private final Tile[][] map;
    public final int COLUMNS;
    public final int ROWS;

    public GameMap(Player[] players) {
        int tilePerPlayer = 12;
        ROWS = (int) Math.ceil(Math.sqrt(((tilePerPlayer + 6) / 2) * players.length));
        COLUMNS = 2 * ROWS;
        map = new Tile[COLUMNS][ROWS];
        for (int x = 0; x < COLUMNS; x++)
            for (int y = 0; y < ROWS; y++)
                map[x][y] = new Tile(x, y);

        Random rand = new Random();
        int x = rand.nextInt(COLUMNS - 1), y = rand.nextInt(ROWS - 1);
        int nonNeutral = 1;
        map[x][y].neutral = false;
        while (nonNeutral != players.length * tilePerPlayer) {
            x = rand.nextInt(COLUMNS - 1);
            y = rand.nextInt(ROWS - 1);
            if (noOfNeighbours(x, y) > 0 && map[x][y].neutral) {
                map[x][y].neutral = false;
                nonNeutral++;
            }
        }

        for (int i = 0; i < players.length; i++) {
            int n = 0;
            while (n < tilePerPlayer) {
                x = rand.nextInt(COLUMNS - 1);
                y = rand.nextInt(ROWS - 1);
                if (!map[x][y].neutral && map[x][y].getOwner() == null) {
                    map[x][y].setOwner(players[i]);
                    map[x][y].setDices(1);
                    n++;
                }
            }

            n = 3 * tilePerPlayer - tilePerPlayer;
            while (n > 0) {
                x = rand.nextInt(COLUMNS - 1);
                y = rand.nextInt(ROWS - 1);
                if (map[x][y].getOwner() == players[i] && map[x][y].incDices() == 1) {
                    n--;
                }
            }
        }
    }

    public GameMap(GameMap gameMap) {
        this.COLUMNS = gameMap.COLUMNS;
        this.ROWS = gameMap.ROWS;
        this.map = new Tile[this.COLUMNS][this.ROWS];
        for (int x = 0; x < COLUMNS; x++)
            for (int y = 0; y < ROWS; y++)
                this.map[x][y] = new Tile(gameMap.map[x][y]);
    }

    public Tile[][] getMap() {
        return map;
    }

    public boolean inBounds(int q, int r) {
        return  r >= 0 &&
                q >= 0 &&
                r < ROWS &&
                q < COLUMNS;
    }

    public Tile getTile(int q, int r) {
        return inBounds(q, r) ? map[q][r] : null;
    }

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

    public static boolean adjacent(Tile t1, Tile t2) {
        if (Math.abs(t1.X - t2.X) <= 1 && Math.abs(t1.Y - t2.Y) <= 1) {
            if (t1.Y % 2 == 1)
                return t1.X <= t2.X || t1.Y == t2.Y; // (t1.X > t2.X) -> (t1.Y == t2.Y) if row is odd
            else
                return t1.X >= t2.X || t1.Y == t2.Y; // (t1.x < t2.X) -> (t1.Y == t2.Y) if row is even
        }
        return false;
    }

    public ArrayList<Tile> getNeighbours(Tile t) {
        return getNeighbours(t.X, t.Y);
    }

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
