package dicewars.scene;

import dicewars.DiceWars;
import dicewars.rendering.GameStateRenderer;
import dicewars.state.GameState;
import dicewars.state.GameState.GameMode;
import dicewars.state.events.GameEvent;
import dicewars.state.events.PlayerAction;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Scene for replay playback
 */
public class ReplayScene implements Scene {
    private final JFrame frame;
    private GameStateRenderer gameStateRenderer;
    private GameState gameState;
    private JButton quitButton = new JButton("Quit to menu");
    private JButton playButton = new JButton("Play");
    private JButton pauseButton = new JButton("Pause");
    private JButton stepButton = new JButton("Step one");
    private JSlider speedControl = new JSlider();
    private JPanel panel = new JPanel();
    private JPanel controlPanel = new JPanel();
    private Timer replayStepTimer = new Timer(500, new ActionListener() {
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
        this.playButton.addActionListener(l -> {
            replayStepTimer.start();
        });
        this.pauseButton.addActionListener(l -> {
            replayStepTimer.stop();
        });
        this.speedControl.setMaximumSize(new Dimension(200, 0));
        this.speedControl.setMinimum(0);
        this.speedControl.setMaximum(990);
        this.speedControl.setValue(500);
        this.speedControl.addChangeListener(l -> {
            replayStepTimer.setDelay(1000 - ((JSlider) l.getSource()).getValue());
        });
        this.controlPanel.setLayout(new FlowLayout());
        this.controlPanel.add(quitButton);
        this.controlPanel.add(new JLabel("Playback speed:"));
        this.controlPanel.add(speedControl);
        this.controlPanel.add(playButton);
        this.controlPanel.add(pauseButton);
        this.controlPanel.add(stepButton);
        this.panel.setLayout(new BorderLayout());
        this.panel.add(controlPanel, BorderLayout.NORTH);
        this.panel.add(gameStateRenderer, BorderLayout.CENTER);
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
        if (event == null) {
            replayStepTimer.stop();
            JOptionPane.showMessageDialog(frame, "Replay file over!");
        }
        if (event instanceof PlayerAction)
            gameStateRenderer.setStatusText(((PlayerAction) event).toString());
    }

    /**
     * Empty the window and add this secene's panel
     */
    @Override
    public void startup() {
        this.frame.getContentPane().removeAll();

        gameStateRenderer.setGameState(this.gameState);

        this.frame.add(panel);

        this.frame.revalidate();
        this.frame.repaint();
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

    @Override
    public void update() {
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
