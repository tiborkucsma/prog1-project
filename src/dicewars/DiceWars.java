package dicewars;

import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.scene.GameCreationScene;
import dicewars.scene.InGameScene;
import dicewars.scene.ReplayScene;
import dicewars.scene.Scene;
import dicewars.state.GameState;
import dicewars.state.GameState.GameMode;

import javax.swing.*;

import dicewars.map.GameMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * The main game class. It is responsible for setting up and running the game.
 */
public class DiceWars {
    private static Timer timer;
    private static final JFrame frame = new JFrame("Dice Wars");
    private static final GameCreationScene GAME_CREATION_SCENE = new GameCreationScene(frame);
    private static final InGameScene IN_GAME_SCENE = new InGameScene(frame);
    private static final ReplayScene REPLAY_SCENE = new ReplayScene(frame);
    static Scene currentState;

    /**
     * The main function of the game.
     * @param args Cmd args
     */
    public static void main(String[] args) {
        frame.setSize(1600, 900);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        switchState(GAME_CREATION_SCENE);

        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (arg0.getSource() == timer) {
                    currentState.render();
                    currentState.update();
                }
            }
        });

        timer.start();
    }

    /**
     * Starts a new game with the given players.
     * @param players Players
     */
    public static void startNewGame(List<Player> players) {
        boolean aiOnly = true;
        for (Player p : players) {
            if (p instanceof HumanPlayer)
            {
                aiOnly = false;
                break;
            }
        }
        IN_GAME_SCENE.setGameState(
            new GameState(
                players,
                new GameMap(players),
                aiOnly ? GameMode.GAME_MODE_AI_ONLY : GameMode.GAME_MODE_NORMAL
            )
        );
        switchState(IN_GAME_SCENE);
    }

    /**
     * Starts a replay with a given state (loaded from a file by GameCreationState)
     * @param state The GameState
     */
    public static void startReplay(GameState state) {
        REPLAY_SCENE.setGameState(state);
        switchState(REPLAY_SCENE);
    }

    /**
     * Ends a game or replay
     */
    public static void endGame() {
        switchState(GAME_CREATION_SCENE);
    }

    /**
     * Switches the current state. Shuts down the last state if there was any.
     * @param newState State to switch to
     */
    public static void switchState(Scene newState) {
        if (currentState != null) currentState.shutdown();
        currentState = newState;
        currentState.startup();
    }
}
