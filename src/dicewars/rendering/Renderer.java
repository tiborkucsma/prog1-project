package dicewars.rendering;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A canvas that can draw texts and polygons
 */
public class Renderer extends JComponent {
    private final Queue<Renderable> renderQueue;

    /**
     * Init render queue with a linked list
     */
    public Renderer() {
        renderQueue = new LinkedList<Renderable>();
    }

    /**
     * Add a renderable to the queue
     * @param r Renderable
     */
    public void addToQueue(Renderable r) {
        renderQueue.add(r);
    }

    /**
     * Render everything in the queue
     * @param g Graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        while (!renderQueue.isEmpty()) {
            renderQueue.poll().render(g);
        }
    }

}
