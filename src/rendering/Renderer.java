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
    }
}
