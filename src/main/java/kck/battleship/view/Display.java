package kck.battleship.view;

import kck.battleship.exceptions.PositionException;
import kck.battleship.model.clases.*;
import kck.battleship.model.enum_.ShipT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Display {

    public static void printTitle() {

        System.out.println("" +
                "\n" +
                " ____    _  _____ _____ _     _____   ____  _   _ ___ ____  ____ \n" +
                "| __ )  / \\|_   _|_   _| |   | ____| / ___|| | | |_ _|  _ \\/ ___| \n" +
                "|  _ \\ / _ \\ | |   | | | |   |  _|   \\___ \\| |_| || || |_) \\___ \\\n" +
                "| |_) / ___ \\| |   | | | |___| |___   ___) |  _  || ||  __/ ___) |\n" +
                "|____/_/   \\_\\_|   |_| |_____|_____| |____/|_| |_|___|_|   |____/ \n");
    }

    public static int printMenu() {
        printTitle();
        System.out.println("\n(1) - Rozpocznij grę");
        System.out.println("(2) - Symuluj grę");
        System.out.println("(3) - Zasady i legenda");
        System.out.println("(4) - Ranking");
        System.out.println("(0) - Wyjście\n");
        return Input.readOption(new Scanner(System.in), "Odpowiedź: ");
    }


    public static void printRules() {
        System.out.println(DisplayColors.ANSI_YELLOW + "\nJak wygrywać:" + DisplayColors.ANSI_RESET);
        System.out.println(DisplayColors.ANSI_WHITE +
                "- Każdy gracz ma pole bitwy reprezentowane przez siatkę 10x10 (domyślną), na której rozmieszcza " + ShipT.sizeAllShips() + " statków, ukrytych przed przeciwnikiem.\n" +
                "- Celem gry jest zatopienie wszystkich statków przeciwnika! Statek zostaje zatopiony, gdy zostanie trafiony raz w każdy obszar, który zajmuje.\n" +
                "- Innymi słowy, statek typu " + ShipT.values()[0] + ", który zajmuje " + ShipT.values()[0].getShipLength() + " pól, zostaje zatopiony po dwóch trafieniach.\n" +
                "- Wszystkie " + ShipT.sizeAllShips() + " statki zajmują łącznie " + ShipT.lengthAllShips() + " pól, więc pierwszy gracz, który odnotuje " + ShipT.lengthAllShips() + " trafień, wygrywa!" +
                DisplayColors.ANSI_RESET);

        System.out.println(DisplayColors.ANSI_YELLOW + "\nRozgrywka:" + DisplayColors.ANSI_RESET);
        System.out.println(DisplayColors.ANSI_WHITE +
                "- Aby grać, postępuj zgodnie z instrukcjami w celu skonfigurowania swoich " + ShipT.sizeAllShips() + " statków w dowolnym układzie (nie są dozwolone układy przekątne ani sąsiadujące z innymi statkami).\n" +
                "- Aby umieścić statek, musisz podać współrzędną początkową (A1-J10 na domyślnej planszy 10x10) i kierunek (pionowy lub poziomy).\n" +
                "- Na przykład: A1 lub B7. Statki nie mogą na siebie nachodzić ani stykać się (atakuje) i musisz pozostać w granicach planszy.\n" +
                "- Gdy obaj gracze skonfigurują swoje statki, bitwa może się rozpocząć!\n" +
                "- Wystrzel torpedy w statki przeciwnika, zgadując współrzędne.\n" +
                "- Wiersze są oznaczane literami A-J, a kolumny liczbami 1-10 (na planszy 10x10).\n" +
                "- Dopuszczalne współrzędne obejmują literę wiersza, a następnie numer kolumny, np. A1, B7, J10 itp.\n" +
                "- Zostaniesz poinformowany, czy trafiłeś w statek, czy nie.\n" +
                "- Zatop wszystkie " + ShipT.sizeAllShips() + " statków komputera, aby wygrać!" +
                DisplayColors.ANSI_RESET);

        System.out.println(DisplayColors.ANSI_YELLOW + "\nLegenda:" + DisplayColors.ANSI_RESET);
        for (ShipT type : ShipT.values()) {
            System.out.println(DisplayColors.ANSI_WHITE +
                    "- (" + DisplayColors.ANSI_YELLOW + Board.SHIP + DisplayColors.ANSI_WHITE + "x" + type.getShipLength() + ")\t: " + type +
                    DisplayColors.ANSI_RESET);
        }
        System.out.println(
                "- ( " + DisplayColors.ANSI_BLUE + Board.WATER + DisplayColors.ANSI_WHITE + " )\t: Woda\n" +
                        "- (" + DisplayColors.ANSI_YELLOW + Board.SHIP + DisplayColors.ANSI_WHITE + ")\t: Statek\n" +
                        "- (" + DisplayColors.ANSI_RED + Board.HIT + DisplayColors.ANSI_WHITE + ")\t: Trafiony statek\n" +
                        "- (" + DisplayColors.ANSI_WHITE + Board.MISS + DisplayColors.ANSI_WHITE + ")\t: Nietrafiony strzał\n");

        System.out.print("\nNaciśnij dowolny klawisz, aby kontynuować...");
        new Scanner(System.in).nextLine();
    }

    public static void printCredits() {
        System.out.println("\nDziękujemy za grę!");
    }


    public static void printError(String message) {
        System.out.println(DisplayColors.ANSI_RED + message + DisplayColors.ANSI_RESET);
    }

    public static void printShot(Player player, Position position, boolean isHit) {
        System.out.println("- " + player.getName() + " strzelił w " + position.toStringEncode(position) + ": " +
                (isHit ? DisplayColors.ANSI_BLUE + "Trafiony!" + DisplayColors.ANSI_RESET :
                        DisplayColors.ANSI_RED + "Nietrafiony!" + DisplayColors.ANSI_RESET));
    }


    public static void printWinner(Player player, Ranking rank) {
        System.out.println(DisplayColors.ANSI_BLUE + "\n✔ " + player.getName() + " wygrał(a)!" + DisplayColors.ANSI_RESET + "\n");
        System.out.println(DisplayColors.ANSI_YELLOW + "Twoj Wynik: " + rank.getPoints() + DisplayColors.ANSI_RESET + "\n");
        System.out.print("\nNaciśnij dowolny klawisz, aby kontynuować...");
        new Scanner(System.in).nextLine();
    }


    public static void printCurrentShip(Ship ship, int numShipLeft) {
        System.out.println("☛ " + ship.getName() + " (" +
                DisplayColors.ANSI_YELLOW + ship.toGraphicLength() + DisplayColors.ANSI_RESET +
                ") x" + numShipLeft);
    }

    public static void printAdjacentBoard(Player pOne, Player pTwo) throws PositionException {
        System.out.println(toStringAdjacentBoard(pOne, pTwo));
    }

    public static String toStringAdjacentBoard(Player pOne, Player pTwo) throws PositionException {
        Board firstBoard = pOne.getBoard();
        Board secondBoard = pTwo.getBoard().getBoardHideShips();
//        Board secondBoard = pTwo.getBoard();
        String letters = "abcdefghij";
        String s = "\n――――――――――――――――――――――――――――――――――\n";
        s += "\n    ";

        for (int i = 1; i <= 10; i++)
            s += i + "   ";

        s += "      ";

        for (int i = 1; i <= 10; i++)
            s += i + "   ";

        s += "\n";
        for (int i = 0; i < firstBoard.getLength(); i++) {
            s += DisplayColors.ANSI_WHITE;
            s += letters.charAt(i) + "   ";
            s += DisplayColors.ANSI_RESET;

            for (int j = 0; j < firstBoard.getLength(); j++) {
                if (firstBoard.getBoard()[i][j] == Board.WATER)
                    s += DisplayColors.ANSI_BLUE + Board.WATER + "   " + DisplayColors.ANSI_RESET;
                else if (firstBoard.getBoard()[i][j] == Board.HIT)
                    s += DisplayColors.ANSI_RED + Board.HIT + "   " + DisplayColors.ANSI_RESET;
                else if (firstBoard.getBoard()[i][j] == Board.MISS) s += Board.MISS + "   " + DisplayColors.ANSI_RESET;
                else s += DisplayColors.ANSI_YELLOW + firstBoard.getBoard()[i][j] + "   " + DisplayColors.ANSI_RESET;
            }

            s += "   ";

            s += DisplayColors.ANSI_WHITE;
            s += DisplayColors.ANSI_WHITE;
            s += letters.charAt(i) + "   ";
            s += DisplayColors.ANSI_RESET;

            for (int j = 0; j < secondBoard.getLength(); j++) {
                if (secondBoard.getBoard()[i][j] == Board.WATER)
                    s += DisplayColors.ANSI_BLUE + Board.WATER + "   " + DisplayColors.ANSI_RESET;
                else if (secondBoard.getBoard()[i][j] == Board.HIT)
                    s += DisplayColors.ANSI_RED + Board.HIT + "   " + DisplayColors.ANSI_RESET;
                else if (secondBoard.getBoard()[i][j] == Board.MISS) s += Board.MISS + "   " + DisplayColors.ANSI_RESET;
                else s += DisplayColors.ANSI_YELLOW + secondBoard.getBoard()[i][j] + "   " + DisplayColors.ANSI_RESET;
            }

            s += "\n";
        }
        s += "\n――――――――――――――――――――――――――――――――――\n";
        return s;
    }

    public static void printBoard(Board board) {
        System.out.println(toStringBoard(board));
    }

    public static String toStringBoard(Board board) {
        String letters = "abcdefghij";
        String s = "\n    ";

        for (int i = 1; i <= 10; i++)
            s += i + "   ";

        s += "\n";
        for (int i = 0; i < board.getLength(); i++) {
            s += DisplayColors.ANSI_WHITE;
            s += letters.charAt(i) + "   ";

            for (int j = 0; j < board.getLength(); j++) {
                if (board.getBoard()[i][j] == Board.WATER)
                    s += DisplayColors.ANSI_BLUE + Board.WATER + "   " + DisplayColors.ANSI_RESET;
                else if (board.getBoard()[i][j] == Board.HIT)
                    s += DisplayColors.ANSI_RED + Board.HIT + "   " + DisplayColors.ANSI_RESET;
                else if (board.getBoard()[i][j] == Board.MISS)
                    s += DisplayColors.ANSI_WHITE + Board.MISS + "   " + DisplayColors.ANSI_RESET;
                else s += DisplayColors.ANSI_YELLOW + board.getBoard()[i][j] + "   " + DisplayColors.ANSI_RESET;
            }
            s += "\n";
        }
        return s;
    }

    public static void printRanking() {
        String fileName = "/Users/mateusz/Desktop/_BattleShips/src/main/java/kck/battleship/model/data/ranking.txt"; // Nazwa pliku, w którym zapisywane są wyniki

        List<Ranking> rankings = new ArrayList<>();

        try {
            File plik = new File(fileName);
            FileReader fileReader = new FileReader(plik);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String linia;
            while ((linia = bufferedReader.readLine()) != null) {
                String[] parts = linia.split(" ");
                if (parts.length == 2) {
                    try {
                        int punkty = Integer.parseInt(parts[0]);
                        String playerName = parts[1];
                        Player player = new Player(playerName);
                        rankings.add(new Ranking(player, punkty));
                    } catch (NumberFormatException e) {
                        System.err.println("Błąd parsowania punktów w linii: " + linia);
                    }
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(rankings, Collections.reverseOrder(Comparator.comparingInt(Ranking::getPoints)));
        System.out.println("\n              " + DisplayColors.ANSI_BLUE + "Ranking" + DisplayColors.ANSI_RESET);
        System.out.println(DisplayColors.ANSI_BLUE+"Position" + DisplayColors.ANSI_CYAN + "    Points        " + DisplayColors.ANSI_GREEN + "Name" + DisplayColors.ANSI_RESET);
        int i = 0;
        for (Ranking ranking : rankings) {
            i++;
            System.out.println("    " + i + "          " + ranking.getPoints() + "          " + ranking.getPlayer().getName());
        }


        System.out.print("\nNaciśnij dowolny klawisz, aby kontynuować...");
        new Scanner(System.in).nextLine();
    }
}
