package org.chelak.colorlines.board;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.View;

import org.chelak.colorlines.R;
import org.chelak.colorlines.game.Location;

import java.util.List;

/**
 * Created by Sergey on 16.01.2016.
 */
public abstract class BaseAnimationTask {

    protected final static double SMALL_BALL_RATE = 0.4;

    protected int[][] board;
    private Bitmap gridBitmap;
    protected Paint paint;
    protected final Paint ballPaint;
    private boolean isBeforeDrawPerformed;

    protected Bitmap ball;
    protected Bitmap smallBall;
    protected Bitmap cell;

    private View view;

    protected final static ColorFilter[] colorFilters = {
            new ColorFilter(1f, 1f, 1f, 0),        // transparent
            new ColorFilter(0.17f, 0.5f, 0.90f),      // blue
            new ColorFilter(1f, 0.63f, 0.53f),     // orange
            new ColorFilter(1f, 0.15f, 0.15f),             // red
            new ColorFilter(1f, 1f, 0.0f),            // yellow
            new ColorFilter(0, 1f, 1f),            // cyan ??
            new ColorFilter(0.2f, 1f, 0.2f),              // green
            new ColorFilter(0.91f, 0.55f, 0.98f)   // purple
    };

    public BaseAnimationTask(int[][] board) {
        this.board = board;

        isBeforeDrawPerformed = false;

        paint = new Paint();

        ballPaint = new Paint();
        ballPaint.setStrokeWidth(2);
        ballPaint.setAntiAlias(true);
        ballPaint.setDither(true);
        ballPaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    public int getBoardSize() {
        return board.length;
    }

    public abstract boolean isAutoremovable();

    public abstract boolean isCompleted();

    protected static Bitmap scaleBitmap(Bitmap original, double rate) {
        int width = (int) (rate * original.getWidth());
        int height = (int) (rate * original.getHeight());
        return Bitmap.createScaledBitmap(original, Math.max(1, width), Math.max(1, height), true);
    }

    private Bitmap createGrid(int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int boardSize = getBoardSize();
        int cellSize = size / boardSize;
        Paint gridPaint = new Paint();

        for (int i = 0; i < boardSize; ++i)
            for (int j = 0; j < boardSize; ++j) {
                canvas.drawBitmap(cell, cellSize * i, cellSize * j, gridPaint);
            }
        return bitmap;
    }

    protected int getCellSize(Canvas canvas) {
        return canvas.getWidth() / getBoardSize();
    }

    protected void drawBalls(Canvas canvas, List<Location> ballsList) {
        for (Location location : ballsList) {
            int i = location.getX();
            int j = location.getY();
            if (board[i][j] != 0) {
                Bitmap bitmap = board[i][j] > 0 ? ball : smallBall;
                drawBall(canvas, location, bitmap, colorFilters[Math.abs(board[i][j])]);
            }
        }
    }

    public void drawBall(Canvas canvas, Location location, Bitmap bitmap, ColorFilter colorFilter) {
        int i = location.getX();
        int j = location.getY();
        int cellSize = getCellSize(canvas);
        float x = cellSize * i;
        float y = cellSize * j;
        canvas.save();
        canvas.translate(0.5f * (cell.getWidth() - bitmap.getWidth()),
                0.5f * (cell.getHeight() - bitmap.getHeight()));
        ballPaint.setColorFilter(colorFilter.createFilter());
        canvas.drawBitmap(bitmap, x, y, ballPaint);
        canvas.restore();
    }


    protected void onBeforeDraw(Canvas canvas) {
        cell = BitmapFactory.decodeResource(view.getResources(), R.drawable.cell);
        ball = BitmapFactory.decodeResource(view.getResources(), R.drawable.ball_template);
        double rate = (1.0 * getCellSize(canvas)) / cell.getWidth();
        cell = scaleBitmap(cell, rate);
        smallBall = scaleBitmap(ball, SMALL_BALL_RATE * rate);
        ball = scaleBitmap(ball, rate);
        isBeforeDrawPerformed = true;
    }

    public void onDraw(Canvas canvas) {
        if (!isBeforeDrawPerformed)
            onBeforeDraw(canvas);
//        if (canvas == null || getBoardSize() == 0)
//            return;
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (gridBitmap == null) {
            gridBitmap = createGrid(canvas.getWidth());
        }
        canvas.drawBitmap(gridBitmap, 0, 0, paint);
        onTaskDraw(canvas);
    }

    protected Paint getPaint() {
        return paint;
    }

    protected abstract void onTaskDraw(Canvas canvas);

    protected static int get(int[][] board, Location location) {
        return board[location.getX()][location.getY()];
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

}
