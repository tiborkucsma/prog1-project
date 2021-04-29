package dicewars.player;

import dicewars.map.GameMap;

import java.awt.*;

/**
 * Super class of all AI players
 */
public abstract class AIPlayer extends Player {
    public AIPlayer(Color color) {
        super(color);
    }

    /**
     * Requests an action from the AI player called by the InGameScene on set intervals if this AI is the current player
     * @param gameMap The map
     * @return Action taken
     */
    public abstract PlayerAction tick(GameMap gameMap);
}
