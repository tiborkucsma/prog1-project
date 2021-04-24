package player;

import map.GameMap;
import map.Tile;

import java.awt.*;
import java.util.Random;

public abstract class Player {
    public final Color color;
    private String status;

    public Player(Color color) {
        this.color = color;
    }

    public void attack(Tile own, Tile target) {
        if (own.dices >= 2 &&
                GameMap.adjacent(own, target) &&
                target.owner != this) {
            StringBuilder sb = new StringBuilder();
            sb.append("Támadás! (" + this.color + " -> " + target.owner.color + ")");
            Random rand = new Random();
            int ownThrow = 0, opponentThrow = 0;
            sb.append(" Saját dobás:");
            for (int i = 0; i < own.dices; i++) {
                int val = rand.nextInt(5) + 1;
                sb.append(" " + val);
                ownThrow += val;
            }
            sb.append(" (" + ownThrow + ")");
            sb.append(" Ellenség dobása:");
            for (int i = 0; i < target.dices; i++) {
                int val = rand.nextInt(5) + 1;
                sb.append(" " + val);
                opponentThrow += val;
            }
            sb.append(" (" + opponentThrow + ")");
            if (ownThrow > opponentThrow) {
                target.owner = this;
                target.dices = own.dices - 1;
            }
            own.dices = 1;
            this.status = sb.toString();
        } else {
            this.status = "Szabálytalan lépés!";
        }
    }

    public void clearStatus() {
        this.status = null;
    }

    public String getStatus() {
        return this.status;
    }
}
