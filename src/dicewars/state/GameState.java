package dicewars.state;

public interface GameState {
    public abstract void startup();
    public abstract void shutdown();

    public abstract void update();
    public abstract void render();
}
