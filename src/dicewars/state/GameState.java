package dicewars.state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dicewars.DiceDistribution;
import dicewars.GameEvent;
import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.player.PlayerAction;

public class GameState implements Serializable {
    public enum GameMode {
        GAME_MODE_NORMAL, GAME_MODE_AI_ONLY, GAME_MODE_REPLAY
    }

    private final List<Player> players;
    private final GameMap map;
    private GameMap originalMap;
    private GameMode mode;
    private List<GameEvent> actionHistory;
    private transient Iterator<GameEvent> actionIt;
    private transient Iterator<Player> playerIt;
    private transient Player currentPlayer;
    
    public GameState(List<Player> players, GameMap map, GameMode mode) {
        this.players = players;
        this.map = map;
        this.originalMap = new GameMap(this.map);
        this.playerIt = this.players.iterator();
        this.currentPlayer = this.playerIt.next();
        this.mode = mode;
        this.actionHistory = new ArrayList<>();
        this.actionIt = null;
    }

    public GameState(List<Player> players, GameMap originalMap, List<GameEvent> events) {
        this.players = players;
        this.map = originalMap;
        this.originalMap = originalMap;
        this.actionHistory = events;
        this.mode = GameMode.GAME_MODE_REPLAY;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public Player getCurrentPlayer() {
        if (currentPlayer == null) nextPlayer();
        return currentPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameMap getMap() {
        return map;
    }

    public void saveReplay(File file) throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
            GameState replay = new GameState(players, originalMap, actionHistory);
            stream.writeObject(replay);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    public void execute(PlayerAction action) {
        if (action.isValid() && action.isActionOf(currentPlayer)) {
            if (mode != GameMode.GAME_MODE_REPLAY)
                actionHistory.add(action.translateToMap(originalMap));
            int ownThrowSum = 0, opponentThrowSum = 0;
            for (int i : action.getOwnThrow()) {
                ownThrowSum += i;
            }
            for (int i : action.getOpponentThrow()) {
                opponentThrowSum += i;
            }
            if (ownThrowSum > opponentThrowSum) {
                action.getTarget().setOwner(action.getAttacker().getOwner());
                action.getTarget().setDices(action.getAttacker().getDices() - 1);
            }
            action.getAttacker().setDices(1);
        } else if (!action.isValid()) {
            System.err.println("Action is invalid!");
        } else {
            System.err.println("This action was not made by the current player!");
        }
    }

    public void endTurn() {
        List<Tile> tilesOfPlayer = map.getTiles(currentPlayer);
        int k = tilesOfPlayer.size() / 2;
        DiceDistribution diceDistribution = map.distributeDices(tilesOfPlayer, k);
        if (mode != GameMode.GAME_MODE_REPLAY)
            actionHistory.add(diceDistribution.translateToMap(originalMap));
        nextPlayer();
    }

    public void nextPlayer() {
        if (playerIt == null) playerIt = players.iterator();
        if (countPlayersAlive() <= 1) return;
        do {
            if (playerIt.hasNext()) {
                currentPlayer = playerIt.next();
            } else {
                playerIt = players.iterator();
                currentPlayer = playerIt.next();
            }
        } while (map.getTiles(currentPlayer).size() == 0);
        System.out.println("Next player (" + currentPlayer + ")");
    }

    public int countPlayersAlive() {
        int res = 0;
        for (Player p : players) {
            if (map.getTiles(p).size() != 0) {
                res++;
            }
        }
        return res;
    }

    public int countHumanPlayersAlive() {
        int res = 0;
        for (Player p : players) {
            if (p instanceof HumanPlayer && map.getTiles(p).size() != 0) {
                res++;
            }
        }
        return res;
    }

    /**
     * Step forward in the action history.
     * Only used in replay mode.
     * @return The processed event, null otherwise (error or end of history)
     */
    public GameEvent stepForward() {
        if (mode == GameMode.GAME_MODE_REPLAY) {
            if (actionIt == null) actionIt = actionHistory.iterator();
            if (actionIt.hasNext()) {
                GameEvent event = actionIt.next();
                if (event instanceof PlayerAction) {
                    execute((PlayerAction) event);
                } else if (event instanceof DiceDistribution) {
                    DiceDistribution dd = (DiceDistribution) event;
                    dd.execute();
                    nextPlayer();
                }
                return event;
            } else {
                return null;
            }
        }
        return null;
    }

}
