public class test_new {
    public static void main(String[] args) {
        
        testIsValidMove();
        testMovePiece();
        testCaptures();
        testComputerPlayer();
        testPositionMethods();
        testGameEngineMethods();
        testRemainingGettersAndSetters();
        
        System.out.println("All tests are fullly done");
    }
    
    static void testIsValidMove() {
        System.out.println("Testing isValidMove():");
        
        board b = new board();
        b.setGrid(new position[9][9]); // clear board for custom setups
        
        // B at (0,0), W at (0,1)
        b.getGrid()[0][0] = new position("A", 1, "B");
        b.getGrid()[0][1] = new position("A", 2, "W");
        
        // Testing board.getGrid() non-void method here to check if grid is actually made
        position[][] layout = b.getGrid();
        String resGrid;
        if (layout != null && layout.length == 9) {
            resGrid = "works";
        } else {
            resGrid = "doesnt work";
        }
        System.out.println("  getGrid() check: " + resGrid);

        // Testing board.getPiece() with an out of bounds check to see if it safely returns null
        position oob = b.getPiece(12, 5);
        String resOob;
        if (oob == null) {
            resOob = "works";
        } else {
            resOob = "doesnt work";
        }
        System.out.println("  getPiece() out of bounds check: " + resOob);
        
        // Test 1: Move horizontally to empty space (using row B so path is clear)
        b.getGrid()[1][0] = new position("B", 1, "B");
        position startH = b.getGrid()[1][0];
        position endH = new position("B", 3, ""); // row 1, col index 2 (B3)
        boolean valid1 = b.isValidMove(startH, endH);
        
        String res1;
        if (valid1) {
            res1 = "works";
        } else {
            res1 = "doesnt work";
        }
        System.out.println("  Move right to empty: " + res1);
        
        // Test 2: Can't move to occupied space
        position start = b.getGrid()[0][0];
        position endOccupied = new position("A", 2, ""); // row 0, col index 1 (A2 has W)
        boolean valid2 = b.isValidMove(start, endOccupied);
        
        String res2;
        if (!valid2) {
            res2 = "works";
        } else {
            res2 = "doesnt work";
        }
        System.out.println("  Can't move to occupied: " + res2);
        
        // Test 3: Move vertically
        position endValidV = new position("C", 1, ""); // row 2, col index 0 (C1)
        boolean valid3 = b.isValidMove(start, endValidV);
        
        String res3;
        if (valid3) {
            res3 = "works";
        } else {
            res3 = "doesnt work";
        }
        System.out.println("  Move down to empty: " + res3);
        
        // Test 4: Diagonal not allowed
        position endDiagonal = new position("B", 2, ""); // row 1, col index 1 (B2)
        boolean valid4 = b.isValidMove(start, endDiagonal);
        
        String res4;
        if (!valid4) {
            res4 = "works";
        } else {
            res4 = "doesnt work";
        }
        System.out.println("  Diagonal blocked: " + res4);
        
        // Test 5: Can't move through piece
        position endBlocked = new position("A", 3, ""); // row 0, col index 2 (A3, blocked by A2)
        boolean valid5 = b.isValidMove(start, endBlocked);
        
        String res5;
        if (!valid5) {
            res5 = "works";
        } else {
            res5 = "doesnt work";
        }
        System.out.println("  Can't move through piece: " + res5);
    }
    
    static void testMovePiece() {
        System.out.println("Testing movePiece():");
        
        board b = new board();
        b.setGrid(new position[9][9]);
        
        // Setup: Simple move
        b.getGrid()[0][0] = new position("A", 1, "B");
        
        position start = b.getGrid()[0][0];
        position end = new position("A", 2, "");
        
        b.movePiece(start, end);
        
        // safe checks to see if old spot is wiped and new spot has the color
        boolean movedCorrectly = (b.getPiece(0, 1) != null && b.getPiece(0, 1).getColor().equals("B") && b.getPiece(0, 0) == null);
        
        String resMove;
        if (movedCorrectly) {
            resMove = "works";
        } else {
            resMove = "doesnt work";
        }
        System.out.println("  Piece at the new position: " + resMove);
    }
    
    static void testCaptures() {
        System.out.println("the captures")        ;
        // Test 1: Horizontal sandwich (1 piece)
        System.out.println("  Horizontal captures:");
        board b1 = new board();
        b1.setGrid(new position[9][9]);
        
        b1.getGrid()[2][0] = new position("C", 1, "B");
        b1.getGrid()[2][1] = new position("C", 2, "W");
        b1.getGrid()[3][2] = new position("D", 3, "B"); // moving piece starts here
        
        position start = b1.getGrid()[3][2];
        position end = new position("C", 3, ""); // lands here to lock the trap!
        b1.movePiece(start, end);
        
        boolean captured = (b1.getPiece(2, 1) == null);
        boolean countRight = (b1.getBlackCapturedCount() == 1);
        
        String resCap1;
        if (captured) {
            resCap1 = "works";
        } else {
            resCap1 = "doesnt work";
        }
        
        String resCount1;
        if (countRight) {
            resCount1 = "works";
        } else {
            resCount1 = "doesnt work";
        }
        System.out.println("    1 piece sandwich: " + resCap1 + " (count: " + resCount1 + ")");
        
        // Test 2: Multiple horizontal capture (3 pieces at once)
        board b2 = new board();
        b2.setGrid(new position[9][9]);
        
        b2.getGrid()[3][0] = new position("D", 1, "B");
        b2.getGrid()[3][1] = new position("D", 2, "W");
        b2.getGrid()[3][2] = new position("D", 3, "W");
        b2.getGrid()[3][3] = new position("D", 4, "W");
        b2.getGrid()[2][4] = new position("C", 5, "B"); // separate moving piece
        
        start = b2.getGrid()[2][4];
        end = new position("D", 5, ""); // lands here to sandwich all 3 white pieces
        b2.movePiece(start, end);
        
        boolean all3removed = (b2.getPiece(3, 1) == null && b2.getPiece(3, 2) == null && b2.getPiece(3, 3) == null);
        boolean count3 = (b2.getBlackCapturedCount() == 3);
        
        String resCap3;
        if (all3removed) {
            resCap3 = "works";
        } else {
            resCap3 = "doesnt work";
        }
        
        String resCount3;
        if (count3) {
            resCount3 = "works";
        } else {
            resCount3 = "doesnt work";
        }
        System.out.println("    3 piece sandwich: " + resCap3 + " (count: " + resCount3 + ")");
        
        // Test 3: Vertical capture (3 pieces)
        System.out.println("  Vertical captures:");
        board b3 = new board();
        b3.setGrid(new position[9][9]);
        
        b3.getGrid()[0][3] = new position("A", 4, "W");
        b3.getGrid()[1][3] = new position("B", 4, "B");
        b3.getGrid()[2][3] = new position("C", 4, "B");
        b3.getGrid()[3][3] = new position("D", 4, "B");
        b3.getGrid()[4][2] = new position("E", 3, "W"); // separate moving white piece
        
        start = b3.getGrid()[4][2];
        end = new position("E", 4, ""); // lands here to complete vertical sandwich
        b3.movePiece(start, end);
        
        boolean all3verticalremoved = (b3.getPiece(1, 3) == null && b3.getPiece(2, 3) == null && b3.getPiece(3, 3) == null);
        boolean count3vertical = (b3.getWhiteCapturedCount() == 3);
        
        String resCap3V;
        if (all3verticalremoved) {
            resCap3V = "works";
        } else {
            resCap3V = "doesnt work";
        }
        
        String resCount3V;
        if (count3vertical) {
            resCount3V = "works";
        } else {
            resCount3V = "doesnt work";
        }
        System.out.println("    3 piece sandwich: " + resCap3V + " (count: " + resCount3V + ")");
        
        // Test 4: Multiple directions capture (Double kill horizontal + vertical at once!)
        System.out.println("  Multiple directions capture:");
        board bMulti = new board();
        bMulti.setGrid(new position[9][9]);
        
        // Set up horizontal sandwich components centered around target spot (2,2)
        bMulti.getGrid()[2][0] = new position("C", 1, "B");
        bMulti.getGrid()[2][1] = new position("C", 2, "W"); // victim 1
        
        // Set up vertical sandwich components centered around target spot (2,2)
        bMulti.getGrid()[0][2] = new position("A", 3, "B");
        bMulti.getGrid()[1][2] = new position("B", 3, "W"); // victim 2
        
        // This is the piece that drops down to lock both traps simultaneously
        bMulti.getGrid()[2][7] = new position("C", 8, "B");
        position startM = bMulti.getGrid()[2][7];
        position endM = new position("C", 3, ""); // lands at (2,2)
        
        bMulti.movePiece(startM, endM);
        boolean doubleKillWorks = (bMulti.getPiece(2, 1) == null && bMulti.getPiece(1, 2) == null);
        String resMulti;
        if (doubleKillWorks) {
            resMulti = "works";
        } else {
            resMulti = "doesnt work";
        }
        System.out.println("    Double sandwich trap at once: " + resMulti);

        // Test 5: Corner capturing rules (All 4 corners)
        System.out.println("  Corner captures:");
        
        // Corner 1: Top-Left (0,0)
        board bc1 = new board();
        bc1.setGrid(new position[9][9]);
        bc1.getGrid()[0][0] = new position("A", 1, "W"); // victim pinned in corner
        bc1.getGrid()[0][1] = new position("A", 2, "B"); // blocking side 1
        bc1.getGrid()[1][1] = new position("B", 2, "B"); // piece to move
        bc1.movePiece(bc1.getGrid()[1][1], new position("B", 1, "")); // moves to (1,0) to block side 2
        
        String resC1;
        if (bc1.getPiece(0, 0) == null) {
            resC1 = "works";
        } else {
            resC1 = "doesnt work";
        }
        System.out.println("    Top-Left Corner (0,0): " + resC1);
        
        // Corner 2: Top-Right (0,8)
        board bc2 = new board();
        bc2.setGrid(new position[9][9]);
        bc2.getGrid()[0][8] = new position("A", 9, "W"); // victim
        bc2.getGrid()[0][7] = new position("A", 8, "B"); // side 1
        bc2.getGrid()[1][7] = new position("B", 8, "B"); // piece to move
        bc2.movePiece(bc2.getGrid()[1][7], new position("B", 9, "")); // moves to (1,8) side 2
        
        String resC2;
        if (bc2.getPiece(0, 8) == null) {
            resC2 = "works";
        } else {
            resC2 = "doesnt work";
        }
        System.out.println("    Top-Right Corner (0,8): " + resC2);

        // Corner 3: Bottom-Left (8,0)
        board bc3 = new board();
        bc3.setGrid(new position[9][9]);
        bc3.getGrid()[8][0] = new position("I", 1, "W"); // victim
        bc3.getGrid()[7][0] = new position("H", 1, "B"); // side 1
        bc3.getGrid()[7][1] = new position("H", 2, "B"); // piece to move
        bc3.movePiece(bc3.getGrid()[7][1], new position("I", 2, "")); // moves to (8,1) side 2
        
        String resC3;
        if (bc3.getPiece(8, 0) == null) {
            resC3 = "works";
        } else {
            resC3 = "doesnt work";
        }
        System.out.println("    Bottom-Left Corner (8,0): " + resC3);

        // Corner 4: Bottom-Right (8,8)
        board bc4 = new board();
        bc4.setGrid(new position[9][9]);
        bc4.getGrid()[8][8] = new position("I", 9, "W"); // victim
        bc4.getGrid()[8][7] = new position("I", 8, "B"); // side 1
        bc4.getGrid()[7][7] = new position("H", 8, "B"); // piece to move
        bc4.movePiece(bc4.getGrid()[7][7], new position("H", 9, "")); // moves to (7,8) side 2
        
        String resC4;
        if (bc4.getPiece(8, 8) == null) {
            resC4 = "works";
        } else {
            resC4 = "doesnt work";
        }
        System.out.println("    Bottom-Right Corner (8,8): " + resC4);

        // Test 6: No capture if not sandwiched
        System.out.println("  No false captures:");
        board b4 = new board();
        b4.setGrid(new position[9][9]);
        
        b4.getGrid()[1][2] = new position("B", 3, "W");
        b4.getGrid()[1][0] = new position("B", 1, "B"); // separate moving piece
        
        start = b4.getGrid()[1][0];
        end = new position("B", 2, ""); // just moves next to it without a second boundary piece
        int capturesBefore = b4.getBlackCapturedCount();
        b4.movePiece(start, end);
        int capturesAfter = b4.getBlackCapturedCount();
        
        boolean noCaptureHappened = (capturesBefore == capturesAfter);
        
        String resNoCap;
        if (noCaptureHappened) {
            resNoCap = "works";
        } else {
            resNoCap = "doesnt work";
        }
        System.out.println("    Pieces not sandwiched: " + resNoCap);
    }
    
    static void testComputerPlayer() {
        System.out.println("Testing computer player:");
        
        System.out.println("  Computer piece selection:");
        board b1 = new board();
        b1.setGrid(new position[9][9]);
        
        b1.getGrid()[0][0] = new position("A", 1, "W");
        b1.getGrid()[0][1] = new position("A", 2, "W");
        
        computer comp = new computer("W");
        comp.makeMove(b1);
        
        // look if one of the white pieces actually shifted away (turned to null)
        boolean computerMoved = (b1.getPiece(0, 0) == null || b1.getPiece(0, 1) == null);
        
        String resComp;
        if (computerMoved) {    
            resComp = "works";
        } else {
            resComp = "doesnt work";
        }
        System.out.println("    Computer makes valid move: " + resComp);
        
        System.out.println("  Computer index generation:");
        board b2 = new board();
        b2.setGrid(new position[9][9]);
        
        b2.getGrid()[4][4] = new position("E", 5, "B");
        
        computer comp2 = new computer("B");
        
        boolean noExceptionThrown = true;
        try {
            comp2.makeMove(b2);
        } catch (ArrayIndexOutOfBoundsException e) {
            noExceptionThrown = false;
        }
        
        String resBounds;
        if (noExceptionThrown) {
            resBounds = "works";
        } else {
            resBounds = "doesnt work";
        }
        System.out.println("    Random indices in bounds: " + resBounds);
    }

    static void testPositionMethods() {
        System.out.println("Testing position class getters (non-void):");
        position p = new position("D", 5, "B");

        // testing getRow()
        String r = p.getRow();
        String resRow;
        if (r.equals("D")) {
            resRow = "works";
        } else {
            resRow = "doesnt work";
        }
        System.out.println("  getRow(): " + resRow);

        // testing getCol()
        int c = p.getCol();
        String resCol;
        if (c == 5) {
            resCol = "works";
        } else {
            resCol = "doesnt work";
        }
        System.out.println("  getCol(): " + resCol);

        // testing getColor()
        String col = p.getColor();
        String resColor;
        if (col.equals("B")) {
            resColor = "works";
        } else {
            resColor = "doesnt work";
        }
        System.out.println("  getColor(): " + resColor);

        // testing getRowIndex() - D should convert to row index 3
        int rIdx = p.getRowIndex();
        String resRIdx;
        if (rIdx == 3) {
            resRIdx = "works";
        } else {
            resRIdx = "doesnt work";
        }
        System.out.println("  getRowIndex(): " + resRIdx);

        // testing getColIndex() - column 5 is array index 4
        int cIdx = p.getColIndex();
        String resCIdx;
        if (cIdx == 4) {
            resCIdx = "works";
        } else {
            resCIdx = "doesnt work";
        }
        System.out.println("  getColIndex(): " + resCIdx);

        // testing toString() formatting
        String str = p.toString();
        String resStr;
        if (str != null && !str.equals("")) {
            resStr = "works";
        } else {
            resStr = "doesnt work";
        }
        System.out.println("  toString(): " + resStr);
    }

    static void testGameEngineMethods() {
        System.out.println("Testing GameEngine methods:");
        GameEngine engine = new GameEngine(null); // pass null since we dont need a live GUI for pure calculations

        // testing getCurrentTurn() at startup
        String turn = engine.getCurrentTurn();
        String resTurn;
        if (turn.equals("B")) {
            resTurn = "works";
        } else {
            resTurn = "doesnt work";
        }
        System.out.println("  getCurrentTurn(): " + resTurn);

        // testing getPieceAt() on default initialized board setup
        position p = engine.getPieceAt(0, 0); 
        String resPiece;
        if (p != null) {
            resPiece = "works";
        } else {
            resPiece = "doesnt work";
        }
        System.out.println("  getPieceAt(): " + resPiece);

        // testing score retrieval getters
        int bCount = engine.getBlackCapturedCount();
        int wCount = engine.getWhiteCapturedCount();
        String resCounts;
        if (bCount == 0 && wCount == 0) {
            resCounts = "works";
        } else {
            resCounts = "doesnt work";
        }
        System.out.println("  Captured count logic paths: " + resCounts);
    }

    static void testRemainingGettersAndSetters() {
        System.out.println("\nTesting all explicit getters and seters:");

        // 1. Testing position class basic modifications
        position p = new position("A", 1, "B");
        
        p.setColor("W");
        String resColor;
        if (p.getColor().equals("W")) {
            resColor = "works";
        } else {
            resColor = "doesnt work";
        }
        System.out.println("  position.setColor() / getColor(): " + resColor);

        p.setRow("C");
        String resRow;
        if (p.getRow().equals("C")) {
            resRow = "works";
        } else {
            resRow = "doesnt work";
        }
        System.out.println("  position.setRow() / getRow(): " + resRow);

        p.setCol(5);
        String resCol;
        if (p.getCol() == 5) {
            resCol = "works";
        } else {
            resCol = "doesnt work";
        }
        System.out.println("  position.setCol() / getCol(): " + resCol);

        // testing the index setters that have actual logic bounds checks
        p.setRowIndex(3); // index 3 is row D
        String resRowIdx;
        if (p.getRow().equals("D") && p.getRowIndex() == 3) {
            resRowIdx = "works";
        } else {
            resRowIdx = "doesnt work";
        }
        System.out.println("  position.setRowIndex() boundary check: " + resRowIdx);

        p.setColIndex(7); // index 7 is column 8
        String resColIdx;
        if (p.getCol() == 8 && p.getColIndex() == 7) {
            resColIdx = "works";
        } else {
            resColIdx = "doesnt work";
        }
        System.out.println("  position.setColIndex() boundary check: " + resColIdx);


        // 2. Testing board class manual capture setters
        board b = new board();
        b.setBlackCapturedCount(4);
        b.setWhiteCapturedCount(2);

        String resBoardGetSet;
        if (b.getBlackCapturedCount() == 4 && b.getWhiteCapturedCount() == 2) {
            resBoardGetSet = "works";
        } else {
            resBoardGetSet = "doesnt work";
        }
        System.out.println("  board score setters / getters path: " + resBoardGetSet);


        // 3. Testing GameEngine config setters
        GameEngine engine = new GameEngine(null);
        engine.setGameMode("PvC");
        
        // checking if setting computer player updates cleanly without throwing exceptions
        boolean engineSetupWorks = true;
        try {
            engine.setComputerPlayer(new computer("W"));
        } catch (Exception e) {
            engineSetupWorks = false;
        }
        
        String resEngine;
        if (engineSetupWorks) {
            resEngine = "works";
        } else {
            resEngine = "doesnt work";
        }
        System.out.println("  GameEngine system configuration updates: " + resEngine);
    }
}