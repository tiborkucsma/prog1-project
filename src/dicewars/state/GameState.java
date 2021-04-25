package dicewars.state;

import dicewars.rendering.Renderer;

public interface GameState {
    public abstract void startup();
    public abstract void shutdown();

    public abstract void update();
    public abstract void render();
}
