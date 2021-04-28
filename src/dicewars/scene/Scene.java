package dicewars.scene;

public interface Scene {
    public abstract void startup();
    public abstract void shutdown();

    public abstract void update();
    public abstract void render();
}
