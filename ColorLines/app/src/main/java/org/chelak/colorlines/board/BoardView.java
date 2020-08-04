package org.chelak.colorlines.board;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.chelak.colorlines.game.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 16.01.2016.
 */
public class BoardView extends SurfaceView {

    private Callbacks callbacks;
    private Thread thread;
    private int boardSize;
    private boolean isRunning;
    private List<BaseAnimationTask> animationTasks;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        animationTasks = new ArrayList<>(10);
        setZOrderOnTop(false);
        SurfaceHolder holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder surfaceHolder) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isRunning = true;
                        Canvas canvas = null;
                        while (isRunning) {
                            if (animationTasks.size() == 0) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    isRunning = false;
                                }
                                continue;
                            }
                            try {
                                canvas = surfaceHolder.lockCanvas(null);
                                if (canvas != null)
                                    synchronized (surfaceHolder) {
                                        BaseAnimationTask task = animationTasks.get(0);
                                        task.onDraw(canvas);
                                        if (task.isCompleted()) {
                                            animationTasks.remove(task);
                                        } else if (animationTasks.size() > 1 && task.isAutoremovable()) {
                                            animationTasks.remove(task);
                                        }
                                        if ( animationTasks.size() == 0 )
                                            callbacks.onDrainAnimationTask();
                                    }
                            } finally {
                                if (canvas != null) {
                                    surfaceHolder.unlockCanvasAndPost(canvas);
                                }
                            }
                        }
                    }
                });
                thread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (thread == null)
                    return;
                // It's seems it's a copy-paste code from the original documentation
                boolean retry = true;
                isRunning = false;
                while (retry) {
                    try {
                        thread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int cellSize = v.getHeight() / boardSize;
                    int x = (int) (event.getX() / cellSize);
                    int y = (int) (event.getY() / cellSize);
                    if (callbacks != null && x < boardSize && y < boardSize)
                        callbacks.onCellTouch(new Location(x, y));
                }
                return true;
            }
        });
    }

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();//View.MeasureSpec.getSize(widthMeasureSpec);
        int height = getMeasuredHeight();//View.MeasureSpec.getSize(heightMeasureSpec);
        //int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int size = Math.min(width, height);
        int cellSize = size / boardSize;
        int realSize = cellSize * boardSize;
        //realSize = View.MeasureSpec.makeMeasureSpec(realSize, MeasureSpec.EXACTLY);
        setMeasuredDimension(realSize, realSize);
    }

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    public void addAnimationTask(BaseAnimationTask animationTask) {
        animationTask.setView(this);
        animationTasks.add(animationTask);
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public interface Callbacks {
        void onCellTouch(Location location);

        void onDrainAnimationTask();
    }

}
