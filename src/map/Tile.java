package map;

import player.Player;

public class Tile {
    public Player owner = null;
    public int dices = 0;
    public boolean neutral = true;
    public final int X;
    public final int Y;

    public Tile(int x, int y) {
        this.X = x;
        this.Y = y;
    }
}
