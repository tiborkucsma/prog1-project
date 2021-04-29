package dicewars.map;

import java.io.Serializable;

import dicewars.player.Player;

/**
 * Describes a tile of a map
 */
public class Tile implements Serializable {
    private Player owner = null;
    private int dices = 1;
    public boolean neutral = true;
    public final int X;
    public final int Y;

    /**
     * Init object
     * @param x x coordinate
     * @param y y coordinate
     */
    public Tile(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    /**
     * Create copy of tile
     * @param t Tile to copy
     */
    public Tile(Tile t) {
        this.X = t.X;
        this.Y = t.Y;
        this.neutral = t.neutral;
        this.dices = t.dices;
        this.owner = t.owner;
    }

    /**
     * Set the number of dices on this tile
     * @param dices new number of dices
     */
    public void setDices(int dices) {
        if (dices >= 1 && dices <= 8) {
            this.dices = dices;
        } else {
            System.err.println("Invalid number of dices (" + dices + ")!");
        }
    }

    /**
     * Increment the number of dices by 1
     * @return How much the number of dices was actually incremented by (0 or 1)
     */
    public int incDices() {
        if (dices < 8) {
            dices++;
            return 1;
        }
        return 0;
    }

    /**
     * Get the number of dices
     * @return Number of dices
     */
    public int getDices() {
        return dices;
    }

    /**
     * Get the owner
     * @return owner
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Set the owner
     * @param owner New owner
     */
    public void setOwner(Player owner) {
        if (!this.neutral && owner != null) this.owner = owner;
    }

}
