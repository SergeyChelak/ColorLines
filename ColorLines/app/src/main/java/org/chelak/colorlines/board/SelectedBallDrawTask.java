package org.chelak.colorlines.board;

import android.graphics.Canvas;

import org.chelak.colorlines.game.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 20.01.2016.
 */
public class SelectedBallDrawTask extends BaseAnimationTask {

    private Location selectedBall;

    private int y, dir;
    private int min, max;
    private List<Location> list;
    private ColorFilter colorFilter;

    public SelectedBallDrawTask(int[][] board, Location selectedBall) {
        super(board);
        this.selectedBall = selectedBall;
    }

    @Override
    public boolean isAutoremovable() {
        return true;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    protected void onBeforeDraw(Canvas canvas) {
        super.onBeforeDraw(canvas);
        dir = 1;
        y = 0;
        colorFilter = colorFilters[get(board, selectedBall)];
        max = (int)(0.5*(cell.getHeight() - ball.getHeight()));
        min = -max;
        int size = getBoardSize();
        list = new ArrayList<>(size * size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j)
                if (board[i][j] != 0 && !selectedBall.equals(i, j)) {
                    list.add(new Location(i, j));
                }
        }
    }

    @Override
    protected void onTaskDraw(Canvas canvas) {
        drawBalls(canvas, list);
        canvas.save();
        if (y >= max)
            dir = -dir;
        else if (y <= min)
            dir = -dir;
        y += dir;
        canvas.translate(0, y);
        drawBall(canvas, selectedBall, ball, colorFilter);
        canvas.restore();
    }
}
