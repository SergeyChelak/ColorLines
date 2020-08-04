package org.chelak.colorlines.game;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey.chelak on 14.01.16.
 */
public abstract class BaseLogic extends Board {

    protected static final String KEY_SCORES = "scores";
    protected static final String KEY_TELEPORT = "teleport";
    protected static final String KEY_GENERATOR = "generator";
    protected static final String KEY_GAME_OVER = "game.over";
    protected static final String KEY_UNDO_DATA = "undo.data";

    private DelegateWrapper delegate;

    private int scores;
    private boolean isGameOver;
    private Generator generator;
    private int teleportationCount;
    private JSONObject undoData;

    public BaseLogic(int boardSize, int colorsCount) {
        super(boardSize);
        generator = new Generator(boardSize, colorsCount);
        delegate = new DelegateWrapper();
    }

    @Override
    public void decode(JSONObject jsonObject) throws JSONException {
        super.decode(jsonObject);
        setScores(jsonObject.getInt(KEY_SCORES));
        setTeleportationCount(jsonObject.getInt(KEY_TELEPORT));
        setGameOver(jsonObject.getBoolean(KEY_GAME_OVER));
        this.generator.decode(jsonObject.getJSONObject(KEY_GENERATOR));
        setUndoData(jsonObject.optJSONObject(KEY_UNDO_DATA));
    }

    private JSONObject encodeBase() throws Exception {
        JSONObject obj = super.encode();
        obj.put(KEY_SCORES, scores);
        obj.put(KEY_TELEPORT, teleportationCount);
        obj.put(KEY_GAME_OVER, isGameOver);
        obj.put(KEY_GENERATOR, generator.encode());
        return obj;
    }

    @Override
    public JSONObject encode() throws Exception {
        // it's a bad solution but simplest
        JSONObject obj = encodeBase();
        obj.putOpt(KEY_UNDO_DATA, undoData);
        return obj;
    }

    @Override
    public void reset() {
        super.reset();
        generator.reset();
        setGameOver(false);
        setScores(0);
        setUndoData(null);
        setTeleportationCount(3);
        generator.chooseNextBallLocation(5);
        putNextBalls();
        generator.chooseNextBallLocation(3);
        delegate.onGameBoardChanged(generator.getBoard());
    }

    private void putNextBalls() {
        delegate.onPutNextBalls(getBoardWithNextBalls());

        int[][] generated = getBoardWithNextBalls();
        int[][] gameBoard = copyBoard();

        List<Location> disappearList = new ArrayList<>();
        for (int i = 0; i < boardSize; ++i)
            for (int j = 0; j < boardSize; ++j) {
                int color = generator.get(i, j);
                if (color < 0) {
                    Location loc = new Location(i, j);
                    set(loc, -color);
                    Board.set(gameBoard, loc, -color);
                    Board.set(generated, loc, -color);
                    if (isFigureCompleted(loc)) {
                        List<Location> list = removeCompletedFigure();
                        disappearList.addAll(list);
                    }
                }
            }
        if (disappearList.size() > 0) {
            addScores(disappearList.size());
            for (Location loc : disappearList)
                gameBoard[loc.getX()][loc.getY()] *= -1;
            delegate.onBallsDisappear(generated, gameBoard, null);
        }
        generator.refreshBoard(board);
    }

    private List<Location> removeCompletedFigure() {
        List<Location> list = new ArrayList<>();
        for (int i = 0; i < boardSize; ++i)
            for (int j = 0; j < boardSize; ++j) {
                if (board[i][j] < 0) {
                    board[i][j] = 0;
                    list.add(new Location(i, j));
                }
                if (generator.get(i, j) >= 0)
                    generator.set(i, j, board[i][j]);
            }
        return list;
    }

    protected abstract boolean isFigureCompleted(Location location);

    public void makeMove(Location from, Location to) {
        makeMove(from, to, false);
    }

    public void makeTeleportation(Location from, Location to) {
        if (teleportationCount > 0) {
            makeMove(from, to, true);
        }
    }

    public void makeMove(Location from, Location to, boolean isTeleportation) {
        WayFinder finder = new WayFinder(board);
        if (isTeleportation || finder.findWay(from, to)) {
            pushUndo();
            if (isTeleportation) {
                setTeleportationCount(teleportationCount - 1);
                delegate.onTeleportation(getBoardWithNextBalls(), from, to);
            } else {
                delegate.onWayDidFound(getBoardWithNextBalls(), finder.getWay());
            }
            swap(from, to);
            if (isFigureCompleted(to)) {
                delegate.onBallsDisappear(getBoardWithNextBalls(), copyBoard(), from);
                int removed = removeCompletedFigure().size();
                addScores(removed);
            } else {
                generator.updateNextBallLocation(from, to, get(to));
                putNextBalls();
                setGameOver(generator.chooseNextBallLocation(3) == 0);
            }
        } else {
            delegate.onWayNotExists();
        }
//        if (isGameOver)
//            delegate.onGameOver();
        delegate.onGameBoardChanged(getBoardWithNextBalls());
    }

    private void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
        if ( isGameOver )
            delegate.onGameOver();
    }

    protected boolean markToRemove(List<List<Location>> stepList) {
        if (stepList.size() > 0) {
            for (List<Location> list : stepList)
                for (Location loc : list) {
                    int value = get(loc);
                    if (value > 0)
                        set(loc, -value);
                }
            return true;
        }
        return false;
    }

    protected int calculateScores(int value) {
        return value;
    }

    private void addScores(int value) {
        setScores(scores + calculateScores(value));
    }

    private void setScores(int value) {
        this.scores = value;
        delegate.onScoresUpdated(value);
    }

    public int getScores() {
        return scores;
    }

    private void pushUndo() {
        try {
            setUndoData(encodeBase());
        } catch (Exception e) {
            //
        }
    }

    public void popUndo() {
        if (undoData != null) {
            try {
                decode(undoData);
                setUndoData(null);
                delegate.onGameBoardChanged(getBoardWithNextBalls());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUndoData(JSONObject object) {
        this.undoData = object;
        delegate.onUndoStateChanged(object != null);
    }

    private void setTeleportationCount(int value) {
        this.teleportationCount = value;
        delegate.onTeleportCountUpdated(value);
    }

    public int getTeleportationCount() {
        return teleportationCount;
    }

    public int[][] getBoardWithNextBalls() {
        return copyBoard(generator.getBoard());
    }

    public void setDelegate(BaseLogicDelegate delegate) {
        this.delegate.setDelegate(delegate);
    }

    public boolean isEmptyCell(Location location) {
        return get(location) == 0;
    }
}
