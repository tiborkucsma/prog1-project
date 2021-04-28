package dicewars;

import java.io.Serializable;

import dicewars.map.GameMap;

public abstract class GameEvent implements Serializable {
    public abstract GameEvent translateToMap(GameMap map);
}
