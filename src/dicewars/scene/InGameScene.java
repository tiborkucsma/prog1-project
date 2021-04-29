package dicewars.scene;

import dicewars.DiceWars;
import dicewars.map.Tile;
import dicewars.player.AIPlayer;
import dicewars.player.HumanPlayer;
import dicewars.rendering.GameStateRenderer;
import dicewars.state.GameState;
import dicewars.state.GameState.GameMode;
import dicewars.state.events.PlayerAction;

import javax.swing.*;

import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * This is the Scene used in-game.
 * The glue between the game state and I/O (rendering and user input)
 */
public class InGameScene implements Scene, MouseListener {
    private final JFrame frame;
    private GameStateRenderer gameStateRenderer;
    private boolean paused = false;
    private GameState gameState;
    private JButton quitButton = new JButton("Quit to menu");
    private JButton saveReplayButton = new JButton("Save replay");
    private JButton endTurnButton = new JButton("End turn");
    private JPanel panel = new JPanel();
    private JPanel controlPanel = new JPanel();
    private JSlider speedControl = new JSlider();
    private Timer aiTimer = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (!paused && !(gameState.getCurrentPlayer() instanceof HumanPlayer)) {
                AIPlayer player = (AIPlayer) gameState.getCurrentPlayer();
                PlayerAction pa = player.tick(gameState.getMap());
                if (pa.isEndTurn()) {
                    gameState.endTurn();
                } else {
                    gameState.execute(pa);
                }
                gameStateRenderer.setStatusText(pa.toString());
            }
        }
    });

    /**
     * Inits the scene
     * @param frame Window to use
     */
    public InGameScene(JFrame frame) {
        this.frame = frame;
        this.gameStateRenderer = new GameStateRenderer();
        this.gameStateRenderer.setVisible(true);
        this.gameStateRenderer.addMouseListener(this);
        this.quitButton.addActionListener(l -> {
            DiceWars.endGame();
        });
        this.endTurnButton.addActionListener(l -> {
            if (gameState.getCurrentPlayer() instanceof HumanPlayer) gameState.endTurn();
        });
        this.saveReplayButton.addActionListener(l -> {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int option = fc.showSaveDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    gameState.saveReplay(file);
                    JOptionPane.showMessageDialog(frame, "Replay saved successfully");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Failed to save replay!");
                }
            }
        });
        this.speedControl.setMaximumSize(new Dimension(200, 0));
        this.speedControl.setMinimum(0);
        this.speedControl.setMaximum(990);
        this.speedControl.setValue(500);
        this.speedControl.addChangeListener(l -> {
            aiTimer.setDelay(1000 - ((JSlider) l.getSource()).getValue());
        });
        this.controlPanel.setLayout(new FlowLayout());
        this.controlPanel.add(quitButton);
        this.controlPanel.add(saveReplayButton);
        this.controlPanel.add(new JLabel("AI speed:"));
        this.controlPanel.add(speedControl);
        this.panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        this.panel.add(controlPanel, BorderLayout.NORTH);
        this.panel.add(gameStateRenderer, BorderLayout.CENTER);
        this.panel.add(endTurnButton, BorderLayout.SOUTH);
    }

    /**
     * Empty the window and add this secene's panel and start the ai tick
     */
    @Override
    public void startup() {
        this.frame.getContentPane().removeAll();

        gameStateRenderer.setGameState(this.gameState);

        this.frame.add(panel);
        
        this.frame.revalidate();
        this.frame.repaint();

        paused = false;

        aiTimer.start();
    }

    /**
     * Empty the window and stop the ai tick
     */
    @Override
    public void shutdown() {
        aiTimer.stop();
        this.frame.getContentPane().removeAll();
        this.frame.revalidate();
        this.frame.repaint();
    }

    /**
     *
     * @param gameState The new game state to load
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void update() {
        int playersAlive = gameState.countPlayersAlive();
        int humanPlayersAlive = gameState.countHumanPlayersAlive();
        if (playersAlive == 1) {
            paused = true;
            String[] options = { "Save game", "Quit" };
            int x = JOptionPane.showOptionDialog(frame, "Game over.",
                    "Info",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (x == 0) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                int option = fc.showSaveDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        gameState.saveReplay(file);
                        JOptionPane.showMessageDialog(frame, "Replay saved successfully");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(frame, "Failed to save replay!");
                    }
                }
            }
            DiceWars.endGame();
        } else if (gameState.getMode() == GameMode.GAME_MODE_NORMAL && humanPlayersAlive == 0) {
            gameState.setMode(GameMode.GAME_MODE_AI_ONLY);
            paused = true;
            String[] options = { "Watch AI players finish...", "End game" };
            int x = JOptionPane.showOptionDialog(frame, "No human players left alive.",
                    "Info",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (x == 0) {
                paused = false;
            } else {
                DiceWars.endGame();
            }
        }
    }

    /**
     * Redraw the game
     */
    @Override
    public void render() {
        gameStateRenderer.revalidate();
        gameStateRenderer.repaint();
    }

    /**
     * Process user mouse input
     * @param e Mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseEvent.BUTTON1) {
            Tile prevSelected = gameStateRenderer.getPrevSelectedTile();
            Tile selected = gameStateRenderer.getSelectedTile();
            if (selected != null && prevSelected != null && selected != prevSelected && prevSelected.getOwner() == gameState.getCurrentPlayer()) {
                PlayerAction pa = new PlayerAction(prevSelected, selected, false);
                gameState.execute(pa);
                gameStateRenderer.setStatusText(pa.toString());
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mousePressed(MouseEvent arg0) {}

    @Override
    public void mouseReleased(MouseEvent arg0) {}

}
