package dicewars.rendering;

import java.awt.*;

/**
 * Text renderable by dicewars.rendering.Renderer
 */
public class RenderableText extends Renderable {
    private String str;
    private int x;
    private int y;
    private Font font;
    private Color color;

    /**
     * Init object
     * @param str Text to render
     * @param x x coordinate
     * @param y y coordinate
     * @param f Font to use
     * @param c Color to use
     */
    public RenderableText(String str, int x, int y, Font f, Color c) {
        this.str = str;
        this.x = x;
        this.y = y;
        this.font = f;
        this.color = c;
    }

    /**
     *
     * @return String rendered by this object
     */
    public String getStr() {
        return str;
    }

    /**
     *
     * @return x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     *
     * @return The font used
     */
    public Font getFont() {
        return font;
    }

    /**
     *
     * @return Color used
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
        g.setFont(font);
        g.drawString(str, x, y);
    }
}
