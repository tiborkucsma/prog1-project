package dicewars.ui;

import java.util.ArrayList;
import java.util.List;

import dicewars.rendering.Renderer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI implements MouseListener {
    private final List<Button> buttons;
    private final List<DialogBox> dialogBoxes;
    private final Renderer renderer;

    public GUI(Renderer renderer) {
        this.renderer = renderer;
        this.buttons = new ArrayList<>();
        this.dialogBoxes = new ArrayList<>();
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
        for (DialogBox b : dialogBoxes) {
            b.render(this.renderer);
        }
    }

    public Button addButton(Button b) {
        buttons.add(b);
        return b;
    }

    public DialogBox addDialogBox(DialogBox box) {
        dialogBoxes.add(box);
        return box;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (dialogBoxes.isEmpty()) {
            for (Button b : buttons) {
                if (b.isHovered()) {
                    b.onClick();
                    break;
                }
            }
        } else {
            DialogBox b = dialogBoxes.get(dialogBoxes.size() - 1);
            if (b.op0.isHovered()) {
                dialogBoxes.remove(b);
                b.op0.onClick();
            } else if (b.op1.isHovered()) {
                dialogBoxes.remove(b);
                b.op1.onClick();
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
