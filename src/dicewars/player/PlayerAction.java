package dicewars.player;

import java.util.Random;

import dicewars.map.GameMap;
import dicewars.map.Tile;

public class PlayerAction {
    private Tile attacker;
    private Tile target;
    private boolean valid;
    private boolean endTurn;
    private boolean executed = false;
    private int[] ownThrow = new int[9];
    private int[] opponentThrow = new int[9];

    public PlayerAction(Tile attacker, Tile target, boolean endTurn) {
        if (attacker == null ||
            target == null ||
            attacker.owner == target.owner ||
            !GameMap.adjacent(attacker, target) ||
            attacker.dices < 2) valid = false;
        else valid = true;
        this.attacker = attacker;
        this.target = target;
        this.endTurn = endTurn;
    }

    public void execute() {
        if (!this.executed) {
            this.executed = true;
            if (this.valid) {
                Random rand = new Random();
                int ownThrow = 0, opponentThrow = 0;
                int i;
                for (i = 0; i < attacker.dices; i++) {
                    int val = rand.nextInt(5) + 1;
                    ownThrow += val;
                    this.ownThrow[i] = val;
                }
                this.ownThrow[i] = -1;
                for (i = 0; i < target.dices; i++) {
                    int val = rand.nextInt(5) + 1;
                    opponentThrow += val;
                    this.opponentThrow[i] = val;
                }
                this.opponentThrow[i] = -1;
                if (ownThrow > opponentThrow) {
                    target.owner = attacker.owner;
                    target.dices = attacker.dices - 1;
                }
                attacker.dices = 1;
            }
        }
    }

    public Tile getAttacker() {
        return attacker;
    }

    public Tile getTarget() {
        return target;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isEndTurn() {
        return endTurn;
    }

    public boolean isExecuted() {
        return executed;
    }

    @Override
    public String toString() {
        if (this.valid && this.executed) {
            StringBuilder sb = new StringBuilder();
            sb.append("Own throw:");
            for (int i = 0; i < 9 && ownThrow[i] != -1; i++) {
                sb.append(" " + ownThrow[i]);
            }

            sb.append(" Opponent throw:");
            for (int i = 0; i < 9 && opponentThrow[i] != -1; i++) {
                sb.append(" " + opponentThrow[i]);
            }

            return sb.toString();
        } else if (this.valid) {
            return "Not executed yet!";
        } else if (this.endTurn) {
            return "End turn!";
        }
        return "Invalid move!";
    }
}
