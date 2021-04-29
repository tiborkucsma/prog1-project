package dicewars.rendering;

import java.awt.*;

/**
 * Polygon renderable by dicewars.rendering.Renderer
 */
public class RenderablePolygon extends Renderable {
    private final Polygon poly;
    private final Color color;

    /**
     * Init object
     * @param p Polygon to use
     * @param c Color to use
     */
    public RenderablePolygon(Polygon p, Color c) {
        this.poly = p;
        this.color = c;
    }

    /**
     *
     * @return The polygon rendered by this object
     */
    public Polygon getPoly() {
        return poly;
    }

    /**
     *
     * @return The color of this object
     */
    public Color getColor() {
        return color;
    }

    /**
     * Render this object
     * @param g Graphics
     */
    @Override
    void render(Graphics g) {
        g.setColor(color);
        g.fillPolygon(poly);
    }
}
