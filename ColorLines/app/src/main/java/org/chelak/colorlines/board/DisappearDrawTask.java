package org.chelak.colorlines.board;

import android.graphics.Canvas;

import org.chelak.colorlines.game.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 23.01.2016.
 */
public class DisappearDrawTask extends BaseAnimationTask {

    private int[][] markedBoard;
    private List<Location> drawList;
    private List<Location> disappearList;
    private float alpha;
    private Location from;

    public DisappearDrawTask(int[][] board, int[][] markedBoard, Location from) {
        super(board);
        this.markedBoard = markedBoard;
        alpha = 1.0f;
        this.from = from;
    }

    @Override
    public boolean isAutoremovable() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return alpha <= 0;
    }

    @Override
    protected void onBeforeDraw(Canvas canvas) {
        super.onBeforeDraw(canvas);
        int size = getBoardSize();
        drawList = new ArrayList<>(size * size);
        disappearList = new ArrayList<>(50);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                Location location = new Location(i, j);
                if (markedBoard[i][j] < 0) {
                    disappearList.add(location);
                } else if (board[i][j] != 0 && !location.equals(from)) {
                    drawList.add(location);
                }
            }
        }
    }

    @Override
    protected void onTaskDraw(Canvas canvas) {
        drawBalls(canvas, drawList);
        alpha-=0.05;
        if ( alpha < 0 )
            return;
        for ( Location location : disappearList ) {
            ColorFilter colorFilter = new ColorFilter(colorFilters[-get(markedBoard, location)]);
            colorFilter.setAlpha(alpha);
            drawBall(canvas, location, ball, colorFilter);
        }
    }
}
