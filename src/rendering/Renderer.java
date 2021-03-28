package rendering;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class Renderer extends JComponent {
    private final Queue<Renderable> renderQueue;

    public Renderer() {
        renderQueue = new LinkedList<Renderable>();
    }

    public void addToQueue(Renderable r) {
        renderQueue.add(r);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        while (!renderQueue.isEmpty()) {
            renderQueue.poll().render(g);
        }
        /*Hexagon h = new Hexagon(50, 50, 50);
        int[][][] map = m.getMap();

        Point cursorPos = MouseInfo.getPointerInfo().getLocation();
        cursorPos.translate(-getLocationOnScreen().x, -getLocationOnScreen().y);

        hoveredTile.x = -1;
        hoveredTile.y = -1;

        for (int y = 0; y < m.ROWS; y++)
        {
            if (y % 2 == 1) h.translate(43, 0);
            else if (y != 0) h.translate(-43, 0);
            for (int x = 0; x < m.COLUMNS; x++)
            {
                if (map[x][y][0] != -1) {
                    switch (map[x][y][0]) {
                        case 1 -> g.setColor(Color.RED);
                        case 2 -> g.setColor(Color.BLUE);
                        case 3 -> g.setColor(Color.ORANGE);
                        case 4 -> g.setColor(Color.YELLOW);
                        default -> g.setColor(Color.GRAY);
                    }
                    if (h.contains(cursorPos))
                    {
                        g.setColor(Color.GREEN);
                        hoveredTile.x = x;
                        hoveredTile.y = y;
                    }
                    g.fillPolygon(h);
                    if (selectedTile.x == x && selectedTile.y == y)
                    {
                        g.setColor(Color.DARK_GRAY);
                        g.fillPolygon(new Hexagon(50 + x * 87 + (y % 2 == 1 ? 43 : 0), 50 + y * 76, 20));
                    }
                }
                h.translate(87, 0);
            }
            h.translate(m.COLUMNS * -87, 76);
        }*/
    }
}
