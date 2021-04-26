package dicewars.map;

import java.io.Serializable;

import dicewars.player.Player;

public class Tile implements Serializable {
    private Player owner = null;
    private int dices = 1;
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

    public void setDices(int dices) {
        if (dices >= 1 && dices <= 8) {
            this.dices = dices;
        } else {
            System.err.println("Invalid number of dices (" + dices + ")!");
        }
    }

    public int incDices() {
        if (dices < 8) {
            dices++;
            return 1;
        }
        return 0;
    }

    public int getDices() {
        return dices;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        if (!this.neutral && owner != null) this.owner = owner;
    }

}
