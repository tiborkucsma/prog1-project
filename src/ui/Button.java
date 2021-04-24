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

public abstract class Button {
    private Polygon shape;
    private Point position;
    private boolean hovered;

    protected abstract void render(Renderer renderer);

    public boolean isHovered() {
        return this.hovered;
    }

    public abstract void onClick();

    public Polygon getShape() {
        return this.shape;
    }
}
