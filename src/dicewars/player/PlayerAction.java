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

    @Override
    public GameEvent translateToMap(GameMap map) {
        Tile tAttacker = map.getTile(attacker.X, attacker.Y);
        Tile tTarget = map.getTile(target.X, target.Y);
        if (tAttacker == null || tTarget == null) return null;
        PlayerAction tAction = new PlayerAction(tAttacker, tTarget, endTurn);
        tAction.valid = this.valid;
        for (int i = 0; i < 9; i++) tAction.ownThrow[i] = this.ownThrow[i];
        for (int i = 0; i < 9; i++) tAction.opponentThrow[i] = this.opponentThrow[i];
        return tAction;
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

    public int[] getOwnThrow() {
        return ownThrow;
    }

    public int[] getOpponentThrow() {
        return opponentThrow;
    }

    @Override
    public String toString() {
        if (this.valid) {
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
