package rendering;

import java.awt.*;

public class RenderableText extends Renderable {
    private String str;
    private int x;
    private int y;
    private Font font;
    private Color color;

    public RenderableText(String str, int x, int y, Font f, Color c) {
        this.str = str;
        this.x = x;
        this.y = y;
        this.font = f;
        this.color = c;
    }

    public String getStr() {
        return str;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Font getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

    @Override
    void render(Graphics g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(str, x, y);
    }
}
