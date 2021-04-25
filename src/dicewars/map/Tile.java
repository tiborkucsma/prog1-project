package dicewars.map;

import java.io.Serializable;

import dicewars.player.Player;

public class Tile implements Serializable {
    public Player owner = null;
    public int dices = 0;
    public boolean neutral = true;
    public final int X;
    public final int Y;

    public Tile(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public Tile(Tile t) {
        this.X = t.X;
        this.Y = t.Y;
        this.neutral = t.neutral;
        this.dices = t.dices;
        this.owner = t.owner;
    }
}
