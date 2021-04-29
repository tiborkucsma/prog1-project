package dicewars.scene;

/**
 * Scenes implement the different guis and gameplay I/O
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
    void render();
}
