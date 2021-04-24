package ui;

import java.util.ArrayList;

import rendering.Renderer;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI implements MouseListener {
    private final ArrayList<Button> buttons;
    private final Renderer renderer;

    public GUI(Renderer renderer) {
        this.renderer = renderer;
        this.buttons = new ArrayList<>();
    }

    public void startup() {
        this.renderer.addMouseListener(this);
    }

    public void shutdown() {
        this.renderer.removeMouseListener(this);
    }

    public void update() {
        
    }

    public void render() {
        for (Button b : buttons) {
            b.render(this.renderer);
        }
    }

    public Button addButton(Button b) {
        buttons.add(b);
        return b;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        for (Button b : buttons) {
            if (b.isHovered()) {
                b.onClick();
                break;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
