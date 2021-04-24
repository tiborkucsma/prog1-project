package state;

import rendering.Renderer;

public abstract class GameState {
    public abstract void startup();
    public abstract void shutdown();

    public abstract void update();
    public abstract void render();
}
