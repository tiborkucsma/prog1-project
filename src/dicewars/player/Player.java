package dicewars.player;

import java.awt.*;
import java.io.Serializable;

public abstract class Player implements Serializable {
    public final Color color;

    public Player(Color color) {
        this.color = color;
    }
}
