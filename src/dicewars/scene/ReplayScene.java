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

    public void setGameState(GameState gameState) {
        if (gameState.getMode() == GameMode.GAME_MODE_REPLAY) {
            this.gameState = gameState;
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid replay game state!");
        }
    }

    public void step() {
        GameEvent event = gameState.stepForward();
        if (event instanceof PlayerAction)
            gameStateRenderer.setStatusText(((PlayerAction) event).toString());
    }

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

    @Override
    public void shutdown() {
        replayStepTimer.stop();
        this.frame.getContentPane().removeAll();
        this.frame.revalidate();
        this.frame.repaint();
    }

    @Override
    public void update() {
        
    }

    @Override
    public void render() {
        gameStateRenderer.revalidate();
        gameStateRenderer.repaint();
    }
    
}
