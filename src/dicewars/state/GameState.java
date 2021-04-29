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

import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.state.events.DiceDistribution;
import dicewars.state.events.GameEvent;
import dicewars.state.events.PlayerAction;

/**
 * Describes the state of a game (players, currentPlayer, map).
 * It also contains data for generating a replay
 * from a normal game (originalMap, actionHistory).
 */
public class GameState implements Serializable {
    /**
     * Describes the game mode:
     * GAME_MODE_NORMAL: Contains at least one alive human player
     * GAME_MODE_AI_ONLY: Does not contain any human players (the game was either started with only ais or all human players lost)
     * GAME_MODE_REPLAY: Only executes actions already in the history, does not take input from players
     */
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

    /**
     * Creates a new game state with the given players, map, and mode
     * @param players Players
     * @param map Map
     * @param mode Game mode
     */
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

    /**
     * Creates a replay with the given players, originalMap, and events
     * @param players Players
     * @param originalMap The original game map (the one that the game was initialized with)
     * @param events Events that happened since the start of the game
     */
    public GameState(List<Player> players, GameMap originalMap, List<GameEvent> events) {
        this.players = players;
        this.map = originalMap;
        this.originalMap = originalMap;
        this.actionHistory = events;
        this.mode = GameMode.GAME_MODE_REPLAY;
    }

    /**
     *
     * @return Mode of the current game
     */
    public GameMode getMode() {
        return mode;
    }

    /**
     * Sets the game mode
     * @param mode New game mode
     */
    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    /**
     *
     * @return Current player or null on error
     */
    public Player getCurrentPlayer() {
        if (currentPlayer == null) nextPlayer();
        return currentPlayer;
    }

    /**
     *
     * @return The list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     *
     * @return The current game map
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * Generates and saves a replay to the specified file
     * @param file Target file
     * @throws IOException
     */
    public void saveReplay(File file) throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
            GameState replay = new GameState(players, originalMap, actionHistory);
            stream.writeObject(replay);
        } catch (FileNotFoundException e) {
            throw e;
        }
    }

    /**
     * Execute a player action (only if it is valid, and was made by the current player)
     * @param action Action to execute
     */
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

    /**
     * Distributes dices to the current player and finds the next player.
     */
    public void endTurn() {
        List<Tile> tilesOfPlayer = map.getTiles(currentPlayer);
        int k = tilesOfPlayer.size() / 2;
        DiceDistribution diceDistribution = map.distributeDices(tilesOfPlayer, k);
        if (mode != GameMode.GAME_MODE_REPLAY)
            actionHistory.add(diceDistribution.translateToMap(originalMap));
        nextPlayer();
    }

    /**
     * Select the next player
     */
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
    }

    /**
     *
     * @return The number of players that still have tiles
     */
    public int countPlayersAlive() {
        int res = 0;
        for (Player p : players) {
            if (map.getTiles(p).size() != 0) {
                res++;
            }
        }
        return res;
    }

    /**
     *
     * @return The number of human players that still have tiles
     */
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
