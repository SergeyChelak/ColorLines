package org.chelak.colorlines.board;

import android.graphics.Canvas;

import org.chelak.colorlines.game.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sergey on 21.01.2016.
 */
public class BallTraceDrawTask extends BaseAnimationTask {

    private List<Location> traces;
    private int position;
    private List<Location> drawList;

    private ColorFilter colorFilter;

    private long prevTime;

    public BallTraceDrawTask(int[][] board, List<Location> traces) {
        super(board);
        this.traces = traces;
        position = 0;
    }

    @Override
    public boolean isAutoremovable() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return position == traces.size();
    }

    @Override
    protected void onBeforeDraw(Canvas canvas) {
        super.onBeforeDraw(canvas);
        Location movingBall = traces.get(0);
        colorFilter = colorFilters[get(board, movingBall)];

        int size = getBoardSize();
        drawList = new ArrayList<>(size * size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j)
                if (board[i][j] != 0 && !movingBall.equals(i, j)) {
                    drawList.add(new Location(i, j));
                }
        }
    }

    @Override
    protected void onTaskDraw(Canvas canvas) {
        drawBalls(canvas, drawList);
        drawBall(canvas, traces.get(position), ball, colorFilter);

        long time = new Date().getTime();
        if ( time - prevTime > 30 && position < traces.size() ) {
            position++;
            prevTime = time;
        }
    }
}
