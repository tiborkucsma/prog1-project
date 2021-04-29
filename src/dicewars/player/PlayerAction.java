package dicewars.player;

import java.util.Random;

import dicewars.GameEvent;
import dicewars.map.GameMap;
import dicewars.map.Tile;

/**
 * Describes the action of a player
 */
public class PlayerAction extends GameEvent {
    private Tile attacker;
    private Tile target;
    private boolean valid;
    private boolean endTurn;
    private int[] ownThrow = new int[9];
    private int[] opponentThrow = new int[9];

    /**
     * Init object
     * @param attacker Attacker tile
     * @param target Target tile
     * @param endTurn End turn with this action?
     */
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

    /**
     * Translates the event to new map. (Fixes references)
     * @param map New map
     * @return New event with correct references for the new map
     */
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

    /**
     * Check if this action was made by the specified player
     * @param p player
     * @return true/false
     */
    public boolean isActionOf(Player p) {
        if (this.attacker.getOwner() == p) return true;
        return false;
    }

    /**
     *
     * @return The attacker tile
     */
    public Tile getAttacker() {
        return attacker;
    }

    /**
     *
     * @return The target tile
     */
    public Tile getTarget() {
        return target;
    }

    /**
     *
     * @return True if the action is valid false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     *
     * @return Should the turn of the player end with this action this method will return true
     */
    public boolean isEndTurn() {
        return endTurn;
    }

    /**
     *
     * @return The attacker's throw
     */
    public int[] getOwnThrow() {
        return ownThrow;
    }

    /**
     *
     * @return The target's throw
     */
    public int[] getOpponentThrow() {
        return opponentThrow;
    }

    /**
     * Generate the string displayed as the status text in game.
     * @return string
     */
    @Override
    public String toString() {
        if (this.valid) {
            StringBuilder sb = new StringBuilder();
            if (attacker.getOwner() instanceof HumanPlayer)
                sb.append("Your throw:");
            else
                sb.append("Attacker throw:");
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
