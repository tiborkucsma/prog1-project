package dicewars.scene;

import dicewars.DiceWars;
import dicewars.GameEvent;
import dicewars.player.AIPlayer;
import dicewars.player.PlayerAction;
import dicewars.rendering.GameStateRenderer;
import dicewars.state.GameState;
import dicewars.state.GameState.GameMode;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Scene for replay playback
 */
public class ReplayScene implements Scene {
    private final JFrame frame;
    private GameStateRenderer gameStateRenderer;
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 20);
    private boolean paused = false;
    private GameState gameState;
    private JButton quitButton = new JButton("Quit to menu");
    private JButton stepButton = new JButton(">");
    private JPanel panel = new JPanel();
    private Timer replayStepTimer = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            step();
        }
    });

    /**
     * Inits the scene
     * @param frame Window to use
     */
    public ReplayScene(JFrame frame) {
        this.frame = frame;
        this.gameStateRenderer = new GameStateRenderer();
        this.gameStateRenderer.setVisible(true);
        this.quitButton.addActionListener(l -> {
            DiceWars.endGame();
        });
        this.stepButton.addActionListener(l -> {
            step();
        });
        this.panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        this.panel.add(quitButton);
        this.panel.add(gameStateRenderer);
        this.panel.add(stepButton);
    }

    /**
     *
     * @param gameState The new game state to load
     */
    public void setGameState(GameState gameState) {
        if (gameState.getMode() == GameMode.GAME_MODE_REPLAY) {
            this.gameState = gameState;
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid replay game state!");
        }
    }

    /**
     * Steps the playback forward
     */
    public void step() {
        GameEvent event = gameState.stepForward();
        if (event instanceof PlayerAction)
            gameStateRenderer.setStatusText(((PlayerAction) event).toString());
    }

    /**
     * Empty the window and add this secene's panel and start the replay tick
     */
    @Override
    public void startup() {
        this.frame.getContentPane().removeAll();

        gameStateRenderer.setGameState(this.gameState);

        this.frame.add(panel);

        this.frame.revalidate();
        this.frame.repaint();

        paused = false;

        replayStepTimer.start();
    }

    /**
     * Empty the window and stop the replay tick
     */
    @Override
    public void shutdown() {
        replayStepTimer.stop();
        this.frame.getContentPane().removeAll();
        this.frame.revalidate();
        this.frame.repaint();
    }

    /**
     * Redraw the game
     */
    @Override
    public void render() {
        gameStateRenderer.revalidate();
        gameStateRenderer.repaint();
    }
    
}
