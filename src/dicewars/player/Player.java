package dicewars.player;

import java.awt.*;
import java.io.Serializable;

/**
 * Super class of all kinds of players
 */
public abstract class Player implements Serializable {
    public final Color color;

    /**
     * Sets color of player
     * @param color Color
     */
    public Player(Color color) {
        this.color = color;
    }
}
