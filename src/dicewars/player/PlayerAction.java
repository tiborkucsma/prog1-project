package dicewars.player;

import java.util.Random;

import dicewars.GameEvent;
import dicewars.map.GameMap;
import dicewars.map.Tile;

public class PlayerAction extends GameEvent {
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
            attacker.getOwner() == target.getOwner() ||
            !GameMap.adjacent(attacker, target) ||
            attacker.getDices() < 2) {
            
            valid = false;
        } else {
            valid = true;
            Random rand = new Random();
            int i;
            for (i = 0; i < attacker.getDices(); i++) {
                int val = rand.nextInt(5) + 1;
                this.ownThrow[i] = val;
            }
            this.ownThrow[i] = -1;
            for (i = 0; i < target.getDices(); i++) {
                int val = rand.nextInt(5) + 1;
                this.opponentThrow[i] = val;
            }
            this.opponentThrow[i] = -1;
        }
        this.attacker = attacker;
        this.target = target;
        this.endTurn = endTurn;
    }

    public PlayerAction(Tile attacker, Tile target, boolean valid, boolean endTurn, int[] ownThrow, int[] opponentThrow) {
        this.attacker = attacker;
        this.target = target;
        this.valid = valid;
        this.endTurn = endTurn;
        this.executed = false;
        for (int i = 0; i < 9; i++) this.ownThrow[i] = ownThrow[i];
        for (int i = 0; i < 9; i++) this.opponentThrow[i] = opponentThrow[i];
    }

    public void execute() {
        this.executed = true;
        if (this.valid) {
            int ownThrowSum = 0, opponentThrowSum = 0;
            for (int i : this.ownThrow) {
                ownThrowSum += i;
            }
            for (int i : this.opponentThrow) {
                opponentThrowSum += i;
            }
            if (ownThrowSum > opponentThrowSum) {
                target.setOwner(attacker.getOwner());
                target.setDices(attacker.getDices() - 1);
            }
            attacker.setDices(1);
        }
    }

    public boolean isActionOf(Player p) {
        if (this.attacker.getOwner() == p) return true;
        return false;
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

    public int[] getOwnThrow() {
        return ownThrow;
    }

    public int[] getOpponentThrow() {
        return opponentThrow;
    }

    @Override
    public String toString() {
        if (this.valid && this.executed) {
            StringBuilder sb = new StringBuilder();
            sb.append("Own throw:");
            int sum = 0;
            for (int i = 0; i < 9 && ownThrow[i] != -1; i++) {
                sb.append(" " + ownThrow[i]);
                sum += ownThrow[i];
            }
            sb.append(" ("+sum+")");

            sb.append(" Opponent throw:");
            sum = 0;
            for (int i = 0; i < 9 && opponentThrow[i] != -1; i++) {
                sb.append(" " + opponentThrow[i]);
                sum += opponentThrow[i];
            }
            sb.append(" ("+sum+")");

            return sb.toString();
        } else if (this.valid) {
            return "Not executed yet!";
        } else if (this.endTurn) {
            return "End turn!";
        }
        return "Invalid move!";
    }
}
