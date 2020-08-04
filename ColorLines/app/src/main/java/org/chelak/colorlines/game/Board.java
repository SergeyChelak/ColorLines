package org.chelak.colorlines.game;

import org.chelak.colorlines.base.Coding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sergey.chelak on 14.01.16.
 */
public class Board implements Coding {

    protected static final String KEY_BOARD = "board";

    protected int[][] board;
    protected int boardSize;

    public Board(int size) {
        this.boardSize = size;
        board = createBoard(size);
    }

    public void decode(JSONObject jsonObject) throws JSONException {
        JSONArray array = (JSONArray)jsonObject.get(KEY_BOARD);
        this.boardSize = array.length();
        this.board = createBoard(boardSize);
        for ( int i = 0; i < boardSize; ++i ) {
            JSONArray row = (JSONArray)array.get(i);
            for ( int j = 0; j < row.length(); ++j )
                board[i][j] = row.getInt(j);
        }
    }

    public JSONObject encode() throws Exception {
        JSONObject object = new JSONObject();
        JSONArray jsonBoard = new JSONArray();
        for ( int[] row : board ) {
            JSONArray array = new JSONArray();
            for ( int value : row )
                array.put(value);

            jsonBoard.put(array);
        }
        object.put(KEY_BOARD, jsonBoard);
        return object;
    }

    public int get(Location location) {
        return board[location.getX()][location.getY()];
    }

    public int get(int i, int j) {
        return board[i][j];
    }

    public void set(int i, int j, int value) {
        board[i][j] = value;
    }

    public void set(Location location, int value) {
        board[location.getX()][location.getY()] = value;
    }

    public boolean isValidRange(Location location) {
        int size = board.length;
        int x = location.getX();
        int y = location.getY();
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    public void reset() {
        for (int i = 0; i < boardSize; ++i)
            for (int j = 0; j < boardSize; ++j)
                board[i][j] = 0;
    }

    public void swap(Location loc1, Location loc2) {
        int tmp = get(loc1);
        set(loc1, get(loc2));
        set(loc2, tmp);
    }

    public int getBoardSize() {
        return boardSize;
    }

    protected int[][] copyBoard() {
        return copyBoard(board);
    }

    public int[][] getBoard() {
        return board;
    }

    private static int[][] createBoard(int size) {
        return new int[size][size];
    }

    protected static int[][] copyBoard(int[][] board) {
        int size = board.length;
        int[][] newBoard = createBoard(size);
        valuesCopy(board, newBoard);
        return newBoard;
    }

    protected static void valuesCopy(int[][] from, int[][] to) {
        int size = from.length;
        for ( int i = 0; i < size; ++i )
            for ( int j = 0; j < size; ++j)
                to[i][j] = from[i][j];
    }

    public static int get(int[][] board, Location loc) {
        return board[loc.getX()][loc.getY()];
    }

    public static void set(int[][] board, Location loc, int value) {
        board[loc.getX()][loc.getY()] = value;
    }
}
