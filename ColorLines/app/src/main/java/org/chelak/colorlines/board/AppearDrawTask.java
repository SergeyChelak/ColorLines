package org.chelak.colorlines.board;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.chelak.colorlines.game.Location;

/**
 * Created by Sergey on 23.01.2016.
 */
public class AppearDrawTask extends StaticBoardDrawTask {

    private final static int MAX_STEPS = 10;

    private int step;
    private double stepSize;
    private double max;
    private double min;

    public AppearDrawTask(int[][] board) {
        super(board);
        step = 0;
    }

    @Override
    public boolean isAutoremovable() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return step >= MAX_STEPS;
    }

    @Override
    protected void onBeforeDraw(Canvas canvas) {
        super.onBeforeDraw(canvas);
        max = 1.0 * getCellSize(canvas) / cell.getWidth();
        min = SMALL_BALL_RATE * max;
        stepSize = (max - min) / MAX_STEPS;

    }

    @Override
    protected void onTaskDraw(Canvas canvas) {
        super.onTaskDraw(canvas);
        double rate = Math.min(max, min + stepSize * step);
        Bitmap bitmap = scaleBitmap(ball, rate);
        for (int i = 0; i < getBoardSize(); ++i)
            for (int j = 0; j < getBoardSize(); ++j)
                if (board[i][j] < 0) {
                    drawBall(canvas, new Location(i, j), bitmap, colorFilters[-board[i][j]]);
                }
        step++;
    }
}
