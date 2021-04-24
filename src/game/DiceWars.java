package game;

import player.AIPlayer;
import player.HumanPlayer;
import player.Player;
import rendering.Renderer;
import state.GameCreationState;
import state.GameState;
import state.InGameState;

import javax.swing.*;

import map.GameMap;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiceWars {
    private static final Renderer renderer = new Renderer();
    private static Timer timer;
    private static final GameCreationState GAME_CREATION_STATE = new GameCreationState(renderer);
    private static final InGameState IN_GAME_STATE = new InGameState(renderer);
    static GameState currentState;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dice Wars");
        frame.setSize(550, 250);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(renderer);

        switchState(GAME_CREATION_STATE);

        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (arg0.getSource() == timer) {
                    currentState.update();
                    currentState.render();

                    renderer.revalidate();
                    renderer.repaint();
                }
            }
        });

        timer.start();
    }

    public static void startNewGame(Player[] players) {
        IN_GAME_STATE.setPlayers(players);
        IN_GAME_STATE.setMap(new GameMap(players));
        switchState(IN_GAME_STATE);
    }

    public static void switchState(GameState newState) {
        if (currentState != null) currentState.shutdown();
        currentState = newState;
        currentState.startup();
    }
}
