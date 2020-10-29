package battleship;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        System.out.println("Player 1, place your ships on the game field");
        Player player1 = new Player("Player 1");
        new Shipyard(player1).createShips(board);

        System.out.println("Press Enter and pass the move to another player");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        System.out.println("Player 2, place your ships on the game field");
        Player player2 = new Player("Player 2");
        new Shipyard(player2).createShips(board);

        String msg = "Press Enter and pass the move to another player";
        Player shootingPlayer = player2;
        Player receivingPlayer = player1;

        while (player1.hasShips() && player2.hasShips()) {
            System.out.println(msg);
            scanner.nextLine();

            shootingPlayer = (shootingPlayer == player1) ? player2 : player1;
            receivingPlayer = (receivingPlayer == player1) ? player2 : player1;

            board.printCells(receivingPlayer, false);
            System.out.println("---------------------");
            board.printCells(shootingPlayer, true);


            Coordinates shot = takeShoot(receivingPlayer.getName());
            msg = receivingPlayer.receiveShot(shot) + "\nPress Enter and pass the move to another player";
        }
        board.printCells(receivingPlayer, true);
        System.out.println("You sank the last ship. You won. Congratulations!");
    }

    private static Coordinates takeShoot(String who) {

        Coordinates shot;
        Scanner scanner = new Scanner(System.in);
        System.out.println(who + ", it's your turn:\n");
        do {
            String buf = scanner.next().toUpperCase();
            try {
                shot = new Coordinates(buf);
            } catch (Exception e) {
                System.out.println("Error! Incorrect coordinates.");
                continue;
            }

            if (shot.getX() < 0 || shot.getX() > 10 || shot.getY() < 1 || shot.getY() > 10) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }

            break;
        } while (true);
        return shot;
    }
}


class Board {
    public void printCells(Player player, boolean showShips) {

        System.out.println();
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {
                if (row == 0 && col == 0) {
                    System.out.print(" ");
                } else if (row == 0) {
                    System.out.print(col);
                } else if (col == 0) {
                    System.out.printf("%c", ('A' + row - 1));
                } else {
                    System.out.print(getSymbol(row, col, player, showShips));
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private char getSymbol(int row, int col, Player player, boolean showShips) {
        Coordinates checkedCell = new Coordinates(col, row);

        if (checkedCell.isIn(player.getReceivedStrikes())) {
            return 'X';
        }
        if (checkedCell.isIn(player.getReceivedMissedShots())) {
            return 'M';
        }
        if (showShips && checkedCell.isIn(player.getCellsOccupiedByShips())) {
            return 'O';
        }
        return '~';
    }
}

class Player {

    final private String name;
    private Ship[] ships = new Ship[0];
    private Coordinates[] receivedStrikes = new Coordinates[0];
    private Coordinates[] receivedMissedShots = new Coordinates[0];

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Coordinates[] getReceivedStrikes() {
        return receivedStrikes;
    }

    public Coordinates[] getReceivedMissedShots() {
        return receivedMissedShots;
    }

    public void setShips(Ship[] ships) {
        this.ships = ships;
    }

    public String receiveShot(Coordinates shot) {

        String msg;
        for (Ship ship : ships) {
            if (ship.receiveShot(shot)) {
                this.receivedStrikes = addShotToArray(this.receivedStrikes, shot);
                this.receivedStrikes = addShotToArray(this.receivedStrikes, shot);
                if (ship.isSunk()) {
                    msg = "You sank a ship! Specify a new target:";
                } else {
                    msg = "You hit a ship!";
                }
                return msg;
            }
        }

        this.receivedMissedShots = addShotToArray(this.receivedMissedShots, shot);
        msg = "You missed!";
        return msg;
    }

    public boolean hasShips() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return true;
            }
        }
        return false;
    }

    private Coordinates[] addShotToArray(Coordinates[] array, Coordinates shot) {
        Coordinates[] updatedArray = new Coordinates[array.length + 1];
        System.arraycopy(array, 0, updatedArray, 0, updatedArray.length - 1);
        updatedArray[updatedArray.length - 1] = shot;
        return updatedArray;
    }


    public Coordinates[] getCellsOccupiedByShips() {
        //count occupied Cells to know how big result will be returned
        int occupiedCellsCounter = 0;

        for (Ship ship : ships) {
            if (ship != null) {
                occupiedCellsCounter += ship.getOccupiedCells().length;
            }
        }
        Coordinates[] result = new Coordinates[occupiedCellsCounter];

        //fulfil result
        occupiedCellsCounter = 0;
        for (Ship ship : ships) {
            if (ship != null) {
                System.arraycopy(ship.getOccupiedCells(), 0, result, occupiedCellsCounter, ship.getOccupiedCells().length);
                occupiedCellsCounter += ship.getOccupiedCells().length;
            }
        }
        return result;
    }
}

class Shipyard {
    Player player;

    public Shipyard(Player player) {
        this.player = player;
    }

    public void createShips(Board board) {
        Ship[] ships = new Ship[5];
        //        ships[0] = new Ship(new Coordinates(3, 2), new Coordinates(7, 2));
        //        ships[1] = new Ship(new Coordinates(9, 3), new Coordinates(9, 6));
        //        ships[2] = new Ship(new Coordinates(7, 4), new Coordinates(7, 6));
        //        ships[3] = new Ship(new Coordinates(3, 5), new Coordinates(3, 7));
        //        ships[4] = new Ship(new Coordinates(6, 9), new Coordinates(7, 9));
        //        this.player.setShips(ships);
        //        board.printCells(this.player);

        board.printCells(this.player, true);

        ships[0] = createShip("Aircraft Carrier");
        this.player.setShips(ships);
        board.printCells(this.player, true);

        ships[1] = createShip("Battleship");
        this.player.setShips(ships);
        board.printCells(this.player, true);

        ships[2] = createShip("Submarine");
        this.player.setShips(ships);
        board.printCells(this.player, true);

        ships[3] = createShip("Cruiser");
        this.player.setShips(ships);
        board.printCells(this.player, true);

        ships[4] = createShip("Destroyer");
        this.player.setShips(ships);
        board.printCells(this.player, true);
    }


    private Ship createShip(String shipType) {

        int requiredCells = 0;
        switch (shipType) {
            case "Aircraft Carrier":
                requiredCells = 5;
                break;
            case "Battleship":
                requiredCells = 4;
                break;
            case "Submarine":
            case "Cruiser":
                requiredCells = 3;
                break;
            case "Destroyer":
                requiredCells = 2;
                break;
        }

        Scanner scanner = new Scanner(System.in);

        Coordinates shipStart;
        Coordinates shipEnd;

        do {
            System.out.printf("Enter the coordinates of the %s (%d cells): ", shipType, requiredCells);
            String bufStart = scanner.next().toUpperCase();
            String bufEnd = scanner.next().toUpperCase();
            try {
                shipStart = new Coordinates(bufStart);
                shipEnd = new Coordinates(bufEnd);
            } catch (Exception e) {
                System.out.println("Error! Incorrect coordinates.");
                continue;
            }

            // checking if coordinates are in between 1 and 10
            if (isShipOutOfBoard(shipStart, shipEnd)) {
                System.out.println("Error! Coordinates must be between A1 and J10");
                continue;
            }

            // checking length
            if (shipStart.getY() == shipEnd.getY()) {
                // horizontal
                //turn the ship if necessary
                if (shipStart.getX() > shipEnd.getX()) {
                    int tmp = shipStart.getX();
                    shipStart.setX(shipEnd.getX());
                    shipEnd.setX(tmp);
                }
                //check length
                if (shipEnd.getX() - shipStart.getX() + 1 != requiredCells) {
                    System.out.printf("Error! Ship must have %d cells.\n", requiredCells);
                    continue;
                }
            } else if (shipStart.getX() == shipEnd.getX()) {
                // vertical
                //turn the ship if necessary
                if (shipStart.getY() > shipEnd.getY()) {
                    int tmp = shipStart.getY();
                    shipStart.setY(shipEnd.getY());
                    shipEnd.setY(tmp);
                }
                //check length
                if (shipEnd.getY() - shipStart.getY() + 1 != requiredCells) {
                    System.out.printf("Error! Ship must have %d cells.\n", requiredCells);
                    continue;
                }
            } else {
                // not horizontal and not vertical
                System.out.println("Error! Ship must in one row or column.");
                continue;
            }

            if (isTooClose(shipStart, shipEnd)) {
                System.out.println("Error! You placed it to close to another one. Try again:");
                continue;
            }

            // every conditions are met
            break;
        } while (true);
        return new Ship(shipStart, shipEnd);

    }

    private boolean isShipOutOfBoard(Coordinates shipStart, Coordinates shipEnd) {
        return shipStart.getX() < 1 || shipStart.getX() > 10 ||
                shipStart.getY() < 1 || shipStart.getY() > 10 ||
                shipEnd.getX() < 1 || shipEnd.getX() > 10 ||
                shipEnd.getY() < 1 || shipEnd.getY() > 10;
    }


    private boolean isTooClose(Coordinates shipStart, Coordinates shipEnd) {
        Coordinates[] proposedCells = new Ship(shipStart, shipEnd).getOccupiedCells();
        for (Coordinates proposedCell : proposedCells) {
            for (Coordinates existingCell : this.player.getCellsOccupiedByShips()) {
                if (Math.abs(proposedCell.getX() - existingCell.getX()) < 2 && Math.abs(proposedCell.getY() - existingCell.getY()) < 2) {
                    return true;
                }
            }
        }
        return false;
    }
}


class Ship {
    final private Coordinates shipStart;
    final private Coordinates shipEnd;
    private Coordinates[] hits = new Coordinates[0];

    public Ship(Coordinates shipStart, Coordinates shipEnd) {
        this.shipStart = shipStart;
        this.shipEnd = shipEnd;
    }

    public boolean receiveShot(Coordinates shot) {
        for (Coordinates cell : getOccupiedCells()) {
            if (cell.isEqual(shot)) {
                if (!shot.isIn(this.hits)) {
                    Coordinates[] oldHits = this.hits;
                    this.hits = new Coordinates[hits.length + 1];
                    System.arraycopy(oldHits, 0, this.hits, 0, oldHits.length);
                    this.hits[this.hits.length - 1] = shot;
                }
                return true;
            }
        }
        return false;
    }

    public boolean isSunk() {
        return hits.length == shipLength();
    }

    private boolean isHorizontal() {
        return shipStart.getY() == shipEnd.getY();
    }

    private int shipLength() {
        return isHorizontal() ? shipEnd.getX() - shipStart.getX() + 1 : shipEnd.getY() - shipStart.getY() + 1;
    }

    public Coordinates[] getOccupiedCells() {
        Coordinates[] occupiedCells = new Coordinates[shipLength()];
        occupiedCells[0] = shipStart;
        for (int ii = 0; ii < occupiedCells.length; ii++) {
            if (isHorizontal()) {
                occupiedCells[ii] = new Coordinates(shipStart.getX() + ii, shipStart.getY());
            } else {
                occupiedCells[ii] = new Coordinates(shipStart.getX(), shipStart.getY() + ii);
            }
        }
        return occupiedCells;
    }
}

class Coordinates {

    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isEqual(Coordinates coordinates) {
        return this.x == coordinates.getX() && this.y == coordinates.getY();
    }

    public boolean isIn(Coordinates[] coordinatesArray) {
        for (Coordinates coordinates : coordinatesArray) {
            if (isEqual(coordinates)) {
                return true;
            }
        }
        return false;
    }

    public Coordinates(String s) {
        char rowLetter = s.charAt(0);
        int row = rowLetter - 'A' + 1;
        this.x = Integer.parseInt(s.substring(1));
        this.y = row;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}