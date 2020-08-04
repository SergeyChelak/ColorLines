package org.chelak.colorlines.game;

import java.util.List;

/**
 * Created by Sergey on 21.01.2016.
 */
public interface BaseLogicDelegate {
    void onGameBoardChanged(int[][] board);

    void onGameOver();

    void onWayNotExists();

    void onScoresUpdated(int value);

    void onTeleportCountUpdated(int value);

    void onTeleportation(int[][] board, Location from, Location to);

    void onWayDidFound(int[][] board, List<Location> traces);

    void onBallsDisappear(int[][] board, int[][] markedBoard, Location from);

    void onPutNextBalls(int[][] board);

    void onUndoStateChanged(boolean isAllowed);
}
