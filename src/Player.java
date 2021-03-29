import javax.management.relation.RelationNotFoundException;
import java.awt.*;
import java.util.Random;

public abstract class Player {
    Color color;

    public void attack(Tile own, Tile target) {
        if (GameMap.adjacent(own, target) &&
                target.owner != this) {
            System.out.println("Can attack!");
            Random rand = new Random();
            int ownThrow = 0, opponentThrow = 0;
            System.out.print("Own throw:");
            for (int i = 0; i < own.dices; i++) {
                int val = rand.nextInt(5) + 1;
                System.out.print(" " + val);
                ownThrow += val;
            }
            System.out.println(" (" + ownThrow + ")");
            System.out.print("Opponent throw:");
            for (int i = 0; i < target.dices; i++) {
                int val = rand.nextInt(5) + 1;
                System.out.print(" " + val);
                opponentThrow += val;
            }
            System.out.println(" (" + opponentThrow + ")");
            if (ownThrow > opponentThrow) {
                target.owner = this;
                target.dices = own.dices - 1;
            }
            own.dices = 1;
        } else {
            System.err.println("Illegal move!");
        }
    }
}
