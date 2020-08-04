package org.chelak.colorlines.board;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.chelak.colorlines.game.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 19.03.2016.
 */
public class TeleportDrawTask extends BaseAnimationTask {

    private final static int MAX_STEPS = 15;

    private Location from;
    private Location to;
    private List<Location> drawList;
    private ColorFilter colorFilter;
    private int step;
    private double stepSize;
    private double max;

    public TeleportDrawTask(int[][] board, Location from, Location to) {
        super(board);
        this.from = from;
        this.to = to;
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
        colorFilter = colorFilters[get(board, from)];

        int size = getBoardSize();
        drawList = new ArrayList<>(size * size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j)
                if (board[i][j] != 0 && !from.equals(i, j)) {       // to should be empty
                    drawList.add(new Location(i, j));
                }
        }

        max = 1.0 * getCellSize(canvas) / cell.getWidth();
        stepSize = max / MAX_STEPS;
    }

    @Override
    protected void onTaskDraw(Canvas canvas) {
        drawBalls(canvas, drawList);

        double rate = Math.min(max, stepSize * step);
        {
            Bitmap bitmap = scaleBitmap(ball, rate);
            drawBall(canvas, to, bitmap, colorFilter);
        }

        {
            Bitmap bitmap = scaleBitmap(ball, Math.max(0, 1 - rate));
            drawBall(canvas, from, bitmap, colorFilter);
        }

        step++;
    }
}
