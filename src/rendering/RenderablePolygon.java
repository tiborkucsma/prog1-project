package rendering;

import java.awt.*;

public class RenderablePolygon extends Renderable {
    private final Polygon poly;
    private final Color color;

    public RenderablePolygon(Polygon p, Color c) {
        this.poly = p;
        this.color = c;
    }

    public Polygon getPoly() {
        return poly;
    }

    public Color getColor() {
        return color;
    }

    @Override
    void render(Graphics g) {
        g.setColor(color);
        g.fillPolygon(poly);
    }
}
