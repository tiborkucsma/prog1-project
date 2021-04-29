package dicewars.scene;

import dicewars.DiceWars;
import dicewars.player.AIPlayerBasic;
import dicewars.player.AIPlayerOffensive;
import dicewars.player.AIPlayerDefensive;
import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * This is basically the main menu of the game.
 * You can either create a new game from this scene,
 * or load a replay.
 */
public class GameCreationScene implements Scene {
    private final JFrame frame;
    private final Color[] playerColors = { Color.CYAN, Color.YELLOW, Color.ORANGE, Color.RED, Color.MAGENTA };
    private JPanel panel = new JPanel();
    private JButton startGameButton = new JButton("Start game");
    private JButton startReplayButton = new JButton("Play replay file");
    private String[] playerTypes = { "None", "Human", "Basic AI", "Offensive AI", "Defensive AI" };
    private JComboBox<String>[] playerSelection = new JComboBox[5];

    /**
     * Inits the scene
     * @param frame Window to use
     */
    public GameCreationScene(JFrame frame) {
        this.frame = frame;
        this.panel.add(startGameButton);
        this.panel.add(startReplayButton);
        for (int i = 0; i < playerSelection.length; i++) {
            playerSelection[i] = new JComboBox<>(playerTypes);
            this.panel.add(playerSelection[i]);
        }
        startGameButton.addActionListener(l -> {
            ArrayList<Player> players = new ArrayList<>();
            int nSelected = 0;
            for (int i = 0; i < playerSelection.length; i++) {
                nSelected++;
                switch (playerSelection[i].getSelectedIndex()) {
                    case 1: players.add(new HumanPlayer(playerColors[i])); break;
                    case 2: players.add(new AIPlayerBasic(playerColors[i])); break;
                    case 3: players.add(new AIPlayerOffensive(playerColors[i])); break;
                    case 4: players.add(new AIPlayerDefensive(playerColors[i])); break;
                    default: nSelected--;
                }
            }
            if (nSelected < 2) JOptionPane.showMessageDialog(frame, "Please select at least 2 players!");
            else dicewars.DiceWars.startNewGame(players);
        });
        startReplayButton.addActionListener(l -> {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int option = fc.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    GameState gs = null;
                    gs = (GameState) ois.readObject();
                    DiceWars.startReplay(gs);
                } catch (IOException|ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(frame, "Failed to load replay file!");
                }
            }
        });
    }

    /**
     * Empty the window and add this secene's panel
     */
    @Override
    public void startup() {
        this.frame.getContentPane().removeAll();

        this.frame.add(panel);

        this.frame.revalidate();
        this.frame.repaint();
    }

    /**
     * Empty the window
     */
    @Override
    public void shutdown() {
        this.frame.getContentPane().removeAll();

        this.frame.revalidate();
        this.frame.repaint();
    }

    @Override
    public void update() {
    }

    @Override
    public void render() {
    }
}
