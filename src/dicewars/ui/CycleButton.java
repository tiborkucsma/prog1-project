package dicewars.ui;

import dicewars.rendering.RenderablePolygon;
import dicewars.rendering.RenderableText;
import dicewars.rendering.Renderer;

import java.awt.*;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class CycleButton extends Button {
    private String[] options;
    private int selected;
    private Polygon shape;
    private Point position;
    private boolean hovered;
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 18);

    public CycleButton(String[] options, Point position) {
        super();
        this.options = options;
        this.position = position;
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        
        int width = 0;
        int height = 0;
        for (String str : options) {
            int currWidth = (int) Math.round(ARIAL_FONT.getStringBounds(str, frc).getWidth());
            int currHeight = (int) Math.round(ARIAL_FONT.getStringBounds(str, frc).getHeight());
            if (currWidth > width) {
                width = currWidth;
                height = currHeight;
            }
        }

        int[] xpoints = { 0, width + 12, width + 12, 0 };
        int[] ypoints = { 0, 0, height, height };
        shape = new Polygon(xpoints, ypoints, 4);
        shape.translate(position.x, position.y);
    }

    @Override
    protected void render(Renderer renderer) {
        Point p = renderer.getMousePosition();
        if (p != null && shape.contains(p)) {
            renderer.addToQueue(new RenderablePolygon(shape, Color.GRAY));
            hovered = true;
        } else {
            renderer.addToQueue(new RenderablePolygon(shape, Color.LIGHT_GRAY));
            hovered = false;
        }
        renderer.addToQueue(new RenderableText(options[selected], position.x + 6, position.y + 16, ARIAL_FONT, Color.BLACK));
    }

    public int getSelectedIndex() {
        return selected;
    }

    public String getSelectedVal() {
        return options[selected];
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public void onClick() {
        selected = (selected + 1) % options.length;
    }

    public Polygon getShape() {
        return this.shape;
    }
}
