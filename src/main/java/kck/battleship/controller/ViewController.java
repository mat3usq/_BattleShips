package kck.battleship.controller;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import kck.battleship.model.clases.BattleField;
import kck.battleship.model.clases.Player;
import kck.battleship.model.clases.Position;
import kck.battleship.model.clases.Ship;
import kck.battleship.view.graphicView.GraphicView;
import kck.battleship.view.textView.TextView;
import kck.battleship.view.View;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ViewController {
    public static int choice;
    private static TextView textView;
    private static GraphicView graphicView;

    public ViewController(int x) {
        choice = x;

        if (x == 1) {
            graphicView = new GraphicView();
            graphicView.printHomePage();
            graphicView.waitForKeyHomePage();
            graphicView.chooseOption(0);
        } else if (x == 2) {
            try {
                Font myFont = new Font("Monospaced", Font.PLAIN, 24);
                AWTTerminalFontConfiguration myFontConfiguration = AWTTerminalFontConfiguration.newInstance(myFont);
                DefaultTerminalFactory dtf = new DefaultTerminalFactory();
                dtf.setForceAWTOverSwing(true);
                dtf.setTerminalEmulatorFontConfiguration(myFontConfiguration);

                Terminal terminal = dtf.createTerminal();
                Screen screen = new TerminalScreen(terminal);
                screen.startScreen();
                screen.setCursorPosition(null);

                textView = new TextView(terminal, screen);

                textView.printHomePage();
                textView.waitForKeyHomePage();
                textView.chooseOption(0);
            } catch (IOException | InterruptedException | GameException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static View getInstance() {
        if (choice == 1)
            return graphicView;

        return textView;
    }

    public static void addShipManually(BattleField battleField, ArrayList<Ship> ships) {
        if (choice == 1) {
            getInstance().addShipsVisually(battleField, ships.get(0), ships);
        } else {
            for (Ship ship : ships)
                addShipText(battleField, ship, ships);
        }
    }

    private static void addShipText(BattleField battleField, Ship ship, ArrayList<Ship> ships) {
        boolean isAdded;
        do {
            getInstance().addShipsVisually(battleField, ship, ships);

            try {
                isAdded = battleField.addShip(ship);
            } catch (GameException e) {
                getInstance().printError(e.getMessage());
                isAdded = false;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }

        } while (!isAdded);
    }

    public static Position getPositionToShot(Player defender, Player attacker) {
        Position shoot = null;

        if (choice == 1) {
            if (attacker.isAI()) {
                boolean isAddHit;
                do {
                    try {
                        shoot = attacker.shoot(defender.getBattleField().getbattleFieldHideShips());
                        isAddHit = defender.addShoot(shoot);
                    } catch (GameException e) {
                        isAddHit = false;
                    }
                } while (!isAddHit);
            } else {
                try {
                    shoot = graphicView.getPositionToShot();
                    defender.addShoot(shoot);
                } catch (GameException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (choice == 2) {
            boolean isAddHit;
            do {
                
                try {
                    shoot = attacker.shoot(defender.getBattleField().getbattleFieldHideShips());
                    isAddHit = defender.addShoot(shoot);
                } catch (GameException e) {
                    if (!attacker.isAI()) textView.printError(e.getMessage());
                    isAddHit = false;
                }
            } while (!isAddHit);
        }

        return shoot;
    }
}


