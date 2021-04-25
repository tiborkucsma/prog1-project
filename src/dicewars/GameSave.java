package dicewars;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.player.Player;
import dicewars.player.PlayerAction;

public class GameSave implements Serializable {
    public List<Player> players;
    public GameMap map;
    public List<PlayerAction> actionHistory;

    public GameSave(List<Player> players, GameMap map) {
        this.players = new ArrayList<>();
        this.players.addAll(players);
        this.map = new GameMap(map);
        this.actionHistory = new ArrayList<>();
    }

    public void addPlayerAction(PlayerAction pa) {
        Tile attacker = pa.getAttacker();
        Tile target = pa.getTarget();
        actionHistory.add(new PlayerAction(
            attacker == null ? null : map.getTile(attacker.X, attacker.Y),
            target == null ? null : map.getTile(target.X, target.Y),
            pa.isEndTurn(),
            pa.getOwnThrow(),
            pa.getOpponentThrow()));
    }
}
