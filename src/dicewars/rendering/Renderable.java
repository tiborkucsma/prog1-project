package dicewars.rendering;

import java.awt.*;

/**
 * Super class of all items renderable by dicewars.rendering.Renderer
 */
public abstract class Renderable {
    /**
     * Render the object
     * @param g Graphics
     */
    abstract void render(Graphics g);
}
