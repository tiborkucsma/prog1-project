package state;

import map.GameMap;
import map.Tile;
import player.AIPlayer;
import player.HumanPlayer;
import player.Player;
import rendering.Renderer;
import ui.Button;
import ui.CycleButton;
import ui.GUI;
import ui.PushButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GameCreationState extends GameState {
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
                game.DiceWars.startNewGame(arr);
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
