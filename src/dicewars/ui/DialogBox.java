package dicewars.ui;

import dicewars.rendering.RenderablePolygon;
import dicewars.rendering.RenderableText;
import dicewars.rendering.Renderer;

import java.awt.*;
import java.awt.Font;

public class DialogBox {
    private Point position;
    private String message;
    protected PushButton op0;
    protected PushButton op1;
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 18);

    public DialogBox(String msg, Point pos, PushButton op0, PushButton op1) {
        this.message = msg;
        this.position = pos;
        this.op0 = op0;
        this.op1 = op1;
        this.op0.setPosition(new Point(this.position.x + 5, this.position.y + 50));
        this.op1.setPosition(new Point(this.position.x + 5, this.position.y + 75));
    }
    
    protected void render(Renderer renderer) {
        int[] xpoints = { 0, 300, 300, 0 };
        int[] ypoints = { 0, 0, 100, 100 };
        Polygon polygon = new Polygon(xpoints, ypoints, 4);
        polygon.translate(position.x, position.y);
        renderer.addToQueue(new RenderablePolygon(polygon, Color.GRAY));
        renderer.addToQueue(new RenderableText(message, position.x, position.y + 20, ARIAL_FONT, Color.BLACK));
        op0.render(renderer);
        op1.render(renderer);
    }

}
