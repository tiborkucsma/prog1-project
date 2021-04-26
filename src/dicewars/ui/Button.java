package dicewars.ui;

import dicewars.rendering.Renderer;

import java.awt.*;

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
