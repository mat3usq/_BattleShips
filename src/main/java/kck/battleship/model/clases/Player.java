package kck.battleship.model.clases;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import kck.battleship.exceptions.BoardException;
import kck.battleship.exceptions.PositionException;
import kck.battleship.model.enum_.Direction;
import kck.battleship.model.enum_.ShipT;
import kck.battleship.view.Display;
import kck.battleship.view.Input;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Player {
    private String name;
    private final boolean isAI;
    private boolean hasAirCrafter = false;
    private int durabilityForceField = 0;
    private Date lastShootTime;
    private final Board board = new Board(10);
    private final ArrayList<Position> shoots = new ArrayList<>();
    private final ArrayList<Position> nextShoots = new ArrayList<>();

    public Player(String name) {
        this.name = name;
        isAI = false;
        lastShootTime = new Date();
    }

    public Player(String name, boolean isAI) {
        this.name = name;
        this.isAI = isAI;
    }

    public String getName() {
        return name;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isAI() {
        return isAI;
    }

    public Date getLastShootTime() {
        return lastShootTime;
    }

    public void setLastShootTime(Date lastShootTime) {
        this.lastShootTime = lastShootTime;
    }

    public int getDurabilityForceField() {
        return durabilityForceField;
    }

    public void setDurabilityForceField(int durabilityForceField) {
        this.durabilityForceField = durabilityForceField;
    }

    public void addShips(Screen screen, Terminal terminal) throws IOException, InterruptedException {
        if (!isAI) {
            ArrayList<Ship> ships = createShips();
            if (hasAirCrafter)
                ships.add(new Ship("LOTNISKOWIEC", 6));

            for (Ship ship : ships)
                addShipManually(screen, terminal, ship, ships);

            Display.printBoard(board);
        } else randAddShips();
    }

    private void addShipManually(Screen screen, Terminal terminal, Ship ship, ArrayList<Ship> ships) throws InterruptedException, IOException {
        boolean isAdded;
        String messagePosition = "Wprowadź współrzędną (np. A1): ";
        String messageDirection = "Wprowadź kierunek (h/v): ";

        do {
            Display.printBoard(board);
            Display.printShip(ship, countShip(ships, ship.getLength()));

            ship.setPosition(Input.readPosition(screen, terminal, board, messagePosition));
            ship.setDirection(Input.readDirection(screen, terminal, messageDirection));

            try {
                isAdded = board.addShip(ship);
            } catch (BoardException | PositionException e) {
                Display.printError(e.toString());
                isAdded = false;
                Thread.sleep(2000);
            }
        } while (!isAdded);
    }

    public void randAddShips() {
        Random random = new Random();
        ArrayList<Ship> ships = createShips();
        if (hasAirCrafter)
            ships.add(new Ship("LOTNISKOWIEC", 6));

        for (Ship ship : ships)
            addShipRandomly(random, ship);
    }

    private void addShipRandomly(Random random, Ship ship) {
        boolean addedSuccessfully = false;
        int failedAttempts = 0;
        int limit = 1000;

        while (failedAttempts <= limit) {
            try {
                ship.setPosition(randPosition());
                ship.setDirection(random.nextBoolean() ? Direction.VERTICAL : Direction.HORIZONTAL);
                addedSuccessfully = board.addShip(ship);
            } catch (BoardException | PositionException e) {
            }

            if (addedSuccessfully)
                break;

            failedAttempts++;
        }

        if (failedAttempts > limit)
            reset();
    }

    private ArrayList<Ship> createShips() {
        ArrayList<Ship> ships = new ArrayList<>();
        for (ShipT type : ShipT.values())
            for (int i = 0; i < type.getNumberShips(); i++)
                ships.add(new Ship(ShipT.toPolishName(type), type.getShipLength()));
        return ships;
    }

    public boolean areShipsStillSailing() {
        return board.getNumberShips() > 0;
    }

    private int countShip(ArrayList<Ship> ships, int length) {
        int count = 0;
        for (Ship ship : ships)
            if (ship.getLength() == length) count++;
        return count;
    }

    public int shipsLeft() {
        return board.getNumberShips();
    }

    private Position randPosition() throws PositionException {
        Random random = new Random();
        int x = random.nextInt(board.getLength());
        int y = random.nextInt(board.getLength());
        return new Position(x, y);
    }

    public boolean addShoot(Position shoot) throws BoardException {
        return board.addHit(shoot);
    }

    public Position ComputerShoot(Board defenderBoard) throws PositionException {
        if (shoots.isEmpty()) return randPosition();
        else {
            nextShoots.addAll(defenderBoard.getAdjacentValidPositions(getLastShoot()));

            if (nextShoots.isEmpty())
                return randPosition();

            Position nextPos = nextShoots.get(0);
            nextShoots.remove(0);
            return nextPos;
        }
    }

    public Position shoot(Screen screen, Terminal terminal, Board defenderBoard) throws PositionException {
        if (isAI) return ComputerShoot(defenderBoard);
        else return Input.readPosition(screen, terminal, board, name + ", gdzie chcesz strzelić? ");
    }

    public void registerShoot(Position position) {
        shoots.add(position);
    }

    public Position getLastShoot() {
        if (shoots.isEmpty()) return null;
        return shoots.get(shoots.size() - 1);
    }

    private void reset() {
        board.reset();
    }

    public void getShop() {
        String fileName = "src/main/java/kck/battleship/model/data/shop.txt"; // Nazwa pliku, w którym zapisywane są wyniki

        try {
            File plik = new File(fileName);
            FileReader fileReader = new FileReader(plik);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String linia;
            while ((linia = bufferedReader.readLine()) != null) {
                String[] parts = linia.split(" ");
                if (parts.length == 2) {
                    try {
                        String playerName = parts[0];
                        int shopOption = Integer.parseInt(parts[1]);

                        if (playerName.equals(name))
                            if (shopOption == 0)
                                hasAirCrafter = true;
                            else if (shopOption == 1) {
                                durabilityForceField = 5;
                            }
                    } catch (NumberFormatException e) {
                        System.err.println("Błąd parsowania punktów w linii: " + linia);
                    }
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        removeShopEntries();
    }

    private void removeShopEntries() {
        String fileName = "src/main/java/kck/battleship/model/data/shop.txt"; // Nazwa pliku, w którym zapisywane są wyniki

        try {
            File inputFile = new File(fileName);
            File tempFile = new File(fileName + "_temp"); // Tymczasowy plik o innej nazwie

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String linia;
            while ((linia = reader.readLine()) != null) {
                String[] parts = linia.split(" ");
                if (parts.length == 2) {
                    try {
                        String playerName = parts[0];
                        int shopOption = Integer.parseInt(parts[1]);

                        // Jeśli warunek nie jest spełniony, zapisz wiersz do pliku tymczasowego
                        if (!(playerName.equals(name) && (shopOption == 0 || shopOption == 1))) {
                            writer.write(linia + System.lineSeparator());
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Błąd parsowania punktów w linii: " + linia);
                    }
                }
            }

            // Zamknij obiekty reader i writer
            reader.close();
            writer.close();

            // Usuń oryginalny plik i zmień nazwę tymczasowego pliku na oryginalny
            if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                System.out.println("Usunięto wiersze spełniające warunek.");
            } else {
                System.err.println("Błąd usuwania wierszy.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}