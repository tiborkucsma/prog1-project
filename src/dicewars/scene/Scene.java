package dicewars.scene;

/**
 * Scenes represent the different guis and handle gameplay I/O
 */
public interface Scene {
    /**
     * Called every time this Scene is switched to.
     */
    void startup();

    /**
     * Called every time this Scene replaced by an other one.
     */
    void shutdown();

    /**
     * Called every 10 milliseconds when this scene is shown.
     */
    void update();

    /**
     * Called every 10 milliseconds when this scene is shown.
     */
    void render();
}
