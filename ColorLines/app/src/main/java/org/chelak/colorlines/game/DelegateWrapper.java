package org.chelak.colorlines.game;

import java.util.List;

/**
 * Created by Sergey on 21.01.2016.
 */
public class DelegateWrapper implements BaseLogicDelegate {

    private BaseLogicDelegate delegate;

    public DelegateWrapper() {
        //
    }

    public BaseLogicDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(BaseLogicDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onGameBoardChanged(int[][] board) {
        if (delegate != null)
            delegate.onGameBoardChanged(board);
    }

    @Override
    public void onGameOver() {
        if (delegate != null)
            delegate.onGameOver();
    }

    @Override
    public void onWayNotExists() {
        if (delegate != null)
            delegate.onWayNotExists();
    }

    @Override
    public void onScoresUpdated(int value) {
        if (delegate != null)
            delegate.onScoresUpdated(value);
    }

    @Override
    public void onTeleportCountUpdated(int value) {
        if (delegate != null)
            delegate.onTeleportCountUpdated(value);
    }

    @Override
    public void onTeleportation(int[][] board, Location from, Location to) {
        if ( delegate != null )
            delegate.onTeleportation(board, from, to);
    }

    @Override
    public void onWayDidFound(int[][] board, List<Location> traces) {
        if (delegate != null) {
            delegate.onWayDidFound(board, traces);
        }
    }

    @Override
    public void onBallsDisappear(int[][] board, int[][] markedBoard, Location from) {
        if (delegate != null) {
            delegate.onBallsDisappear(board, markedBoard, from);
        }
    }

    @Override
    public void onPutNextBalls(int[][] board) {
        if (delegate != null)
            delegate.onPutNextBalls(board);
    }

    @Override
    public void onUndoStateChanged(boolean isAllowed) {
        if (delegate != null)
            delegate.onUndoStateChanged(isAllowed);
    }
}
