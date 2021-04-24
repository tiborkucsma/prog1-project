package ui;

import rendering.RenderablePolygon;
import rendering.RenderableText;
import rendering.Renderer;

import java.awt.*;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.Policy;

public abstract class PushButton extends Button {
    public String str;
    private Polygon shape;
    private Point position;
    private boolean hovered;
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 18);

    public PushButton(String str, Point position) {
        this.str = str;
        this.position = position;
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        int width = (int) Math.round(ARIAL_FONT.getStringBounds(str, frc).getWidth());
        int height = (int) Math.round(ARIAL_FONT.getStringBounds(str, frc).getHeight());
        int[] xpoints = { 0, width + 12, width + 12, 0 };
        int[] ypoints = { 0, 0, height, height };
        shape = new Polygon(xpoints, ypoints, 4);
        shape.translate(position.x, position.y);
    }

    protected void render(Renderer renderer) {
        Point p = renderer.getMousePosition();
        if (p != null && shape.contains(p)) {
            renderer.addToQueue(new RenderablePolygon(shape, Color.GRAY));
            hovered = true;
        } else {
            renderer.addToQueue(new RenderablePolygon(shape, Color.LIGHT_GRAY));
            hovered = false;
        }
        renderer.addToQueue(new RenderableText(str, position.x + 6, position.y + 16, ARIAL_FONT, Color.BLACK));
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public abstract void onClick();

    public Polygon getShape() {
        return this.shape;
    }
}
