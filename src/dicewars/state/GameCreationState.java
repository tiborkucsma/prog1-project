package dicewars.state;

import dicewars.DiceWars;
import dicewars.GameSave;
import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.player.AIPlayer;
import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.rendering.Renderer;
import dicewars.ui.Button;
import dicewars.ui.CycleButton;
import dicewars.ui.GUI;
import dicewars.ui.PushButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class GameCreationState implements GameState {
    private final Renderer renderer;
    private final Color[] playerColors = { Color.CYAN, Color.YELLOW, Color.ORANGE, Color.RED, Color.MAGENTA };
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 20);
    private final GUI gui;
    private final CycleButton[] playerSelection;

    public GameCreationState(Renderer r) {
        this.renderer = r;
        gui = new GUI(this.renderer);
        gui.addButton(new PushButton("Játék indítása", new Point(0, 0)) {
            @Override
            public void onClick() {
                ArrayList<Player> players = new ArrayList<>();
                for (int i = 0; i < playerSelection.length; i++) {
                    if (playerSelection[i].getSelectedIndex() == 1) {
                        players.add(new HumanPlayer(playerColors[i]));
                    } else if (playerSelection[i].getSelectedIndex() == 2) {
                        players.add(new AIPlayer(playerColors[i]));
                    }
                }
                Player[] arr = new Player[players.size()];
                arr = players.toArray(arr);
                dicewars.DiceWars.startNewGame(arr);
            }
        });
        gui.addButton(new PushButton("Play replay file", new Point(150, 0)) {
            @Override
            public void onClick() {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                int option = fc.showOpenDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        GameSave gs = null;
                        gs = (GameSave) ois.readObject();
                        DiceWars.startReplay(gs);
                    } catch (FileNotFoundException e) {
                        JDialog dialog = new JDialog();
                        dialog.setTitle("Error");
                        JLabel l = new JLabel("Failed to load replay file, file not found!");
                        dialog.add(l);
                        dialog.setSize(400, 200);
                        dialog.setVisible(true);
                    } catch (IOException e) {
                        JDialog dialog = new JDialog();
                        dialog.setTitle("Error");
                        JLabel l = new JLabel("Failed to load replay file, IO exception!");
                        dialog.add(l);
                        dialog.setSize(400, 200);
                        dialog.setVisible(true);
                    } catch (ClassNotFoundException e) {
                        JDialog dialog = new JDialog();
                        dialog.setTitle("Error");
                        JLabel l = new JLabel("Failed to load replay file, class not found!");
                        dialog.add(l);
                        dialog.setSize(400, 200);
                        dialog.setVisible(true);
                    }
                }
            }
        });
        this.playerSelection = new CycleButton[5];
        for (int i = 0; i < 5; i++) {
            String[] opts = { "Nincs", "Ember", "AI" };
            this.playerSelection[i] = (CycleButton) gui.addButton(new CycleButton(opts, new Point(0, 50 + i * 25)));
        }
    }

    @Override
    public void startup() {
        gui.startup();
    }

    @Override
    public void shutdown() {
        gui.shutdown();
    }

    @Override
    public void update() {
        gui.update();
    }

    @Override
    public void render() {
        gui.render();
    }
}
