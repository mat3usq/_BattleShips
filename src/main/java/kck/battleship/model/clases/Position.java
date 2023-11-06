package kck.battleship.model.clases;

import kck.battleship.exceptions.PositionException;

import java.util.Random;

public class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) throws PositionException {
        if (row < 0 || column < 0)
            throw new PositionException("Wprowadzaj dane w formacie [rząd][kolumna]");
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static Position randPosition() throws PositionException {
        Random random = new Random();
        int x = random.nextInt(BattleField.getLength());
        int y = random.nextInt(BattleField.getLength());
        return new Position(x, y);
    }

    public String toStringEncode(Position position) {
        return "(" + (char) ('a' + position.getRow()) + "," + (position.getColumn() + 1) + ")";
    }
}
