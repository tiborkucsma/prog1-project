package dicewars.player;

import dicewars.map.GameMap;

import java.awt.*;

public abstract class AIPlayer extends Player {
    public AIPlayer(Color color) {
        super(color);
    }

    public abstract PlayerAction tick(GameMap gameMap);
}
