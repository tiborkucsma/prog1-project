package dicewars.scene;

import dicewars.DiceWars;
import dicewars.GameSave;
import dicewars.player.AIPlayerEasy;
import dicewars.player.AIPlayerHard;
import dicewars.player.AIPlayerMedium;
import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class GameCreationScene implements Scene {
    private final JFrame frame;
    private final Color[] playerColors = { Color.CYAN, Color.YELLOW, Color.ORANGE, Color.RED, Color.MAGENTA };
    private JPanel panel = new JPanel();
    private JButton startGameButton = new JButton("Start game");
    private JButton startReplayButton = new JButton("Play replay file");
    private String[] playerTypes = { "None", "Human", "AI Easy", "AI Medium", "AI Hard" };
    private JComboBox<String>[] playerSelection = new JComboBox[5];

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
            for (int i = 0; i < playerSelection.length; i++) {
                switch (playerSelection[i].getSelectedIndex()) {
                    case 1: players.add(new HumanPlayer(playerColors[i])); break;
                    case 2: players.add(new AIPlayerEasy(playerColors[i])); break;
                    case 3: players.add(new AIPlayerMedium(playerColors[i])); break;
                    case 4: players.add(new AIPlayerHard(playerColors[i])); break;
                    default:
                }
            }
            dicewars.DiceWars.startNewGame(players);
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
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(frame, "Failed to load replay file, file not found!");
                    e.printStackTrace();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Failed to load replay file, IO exception!");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(frame, "Failed to load replay file, class not found!");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void startup() {
        this.frame.getContentPane().removeAll();

        this.frame.add(panel);

        this.frame.revalidate();
        this.frame.repaint();
    }

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
