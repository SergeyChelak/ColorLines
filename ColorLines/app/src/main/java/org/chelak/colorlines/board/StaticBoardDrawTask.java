package org.chelak.colorlines.board;

import android.graphics.Canvas;

import org.chelak.colorlines.game.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 16.01.2016.
 */
public class StaticBoardDrawTask extends BaseAnimationTask {


    public StaticBoardDrawTask(int[][] board) {
        super(board);
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public boolean isAutoremovable() {
        return true;
    }

    @Override
    protected void onTaskDraw(Canvas canvas) {
        int size = getBoardSize();
        List<Location> list = new ArrayList<>(size*size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j)
                if (board[i][j] != 0) {
                    list.add(new Location(i,j));
                }
        }
        drawBalls(canvas, list);
    }

}
