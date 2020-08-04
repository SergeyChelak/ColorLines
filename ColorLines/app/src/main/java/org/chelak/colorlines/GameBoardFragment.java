package org.chelak.colorlines;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.chelak.colorlines.base.BaseFragment;
import org.chelak.colorlines.base.Preferences;
import org.chelak.colorlines.board.AppearDrawTask;
import org.chelak.colorlines.board.BallTraceDrawTask;
import org.chelak.colorlines.board.BoardView;
import org.chelak.colorlines.board.DisappearDrawTask;
import org.chelak.colorlines.board.SelectedBallDrawTask;
import org.chelak.colorlines.board.StaticBoardDrawTask;
import org.chelak.colorlines.board.TeleportDrawTask;
import org.chelak.colorlines.dialogs.BaseDialog;
import org.chelak.colorlines.dialogs.ConfirmDialog;
import org.chelak.colorlines.dialogs.GameOverDialog;
import org.chelak.colorlines.game.BaseLogic;
import org.chelak.colorlines.game.BaseLogicDelegate;
import org.chelak.colorlines.game.LinesLogic;
import org.chelak.colorlines.game.Location;
import org.chelak.colorlines.game.RingsLogic;
import org.chelak.colorlines.game.SquaresLogic;

import java.util.List;

/**
 * Created by Sergey on 16.01.2016.
 */
public class GameBoardFragment extends BaseFragment {

    public interface GameEventListener {
        void onGameOver();

        void onGameStarted(long mode);

        void onMenuSelected();
    }

    private final static String BEST_SCORES = "best.scores";
    private final static String GAME_MODE = "game.mode";
    private final static String LOCATION_FROM = "location.from";
    private final static String GAME_BOARD = "game.board";
    private final static String TELEPORT_MODE = "teleport.mode";

    private BaseLogic logic;
    private Location from;
    private boolean isTeleportMode;

    private GameEventListener listener;

    public static GameBoardFragment getInstance() {
        return new GameBoardFragment();
    }

    @Override
    public int getFragmentId() {
        return R.layout.fragment_gameboard;
    }

    private ConfirmDialog.ConfirmDialogListener getConfirmListener() {
        return new ConfirmDialog.ConfirmDialogListener() {
            @Override
            public void onSelectedPositive(ConfirmDialog dialog) {
                setGameMode(dialog.getNumericTag());
            }

            @Override
            public void onSelectedNegative(ConfirmDialog dialog) {
                // do nothing
            }
        };
    }

    private GameOverDialog.GameOverDialogListener getGameOverListener() {
        return new GameOverDialog.GameOverDialogListener() {
            @Override
            public void onNewGame() {
                logic.reset();
            }
        };
    }

    private void restoreListeners() {
        Fragment fragment = getFragmentManager().findFragmentByTag("dialog");
        if (fragment == null)
            return;
        if (fragment instanceof ConfirmDialog) {
            ConfirmDialog dlg = (ConfirmDialog) fragment;
            dlg.setListener(getConfirmListener());
        } else if (fragment instanceof GameOverDialog) {
            GameOverDialog dlg = (GameOverDialog) fragment;
            dlg.setListener(getGameOverListener());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreListeners();
        final BoardView boardView = getGameBoardView();
        if (logic == null) {
            setupGameLogic();
        }
        boardView.setCallbacks(new BoardView.Callbacks() {
            @Override
            public void onCellTouch(Location location) {
                boolean isEmptyCell = logic.isEmptyCell(location);
                if (!isEmptyCell) {
                    if (location.equals(from)) {
                        from = null;
                        invalidateBoard();
                    } else {
                        from = location;
                        boardView.addAnimationTask(new SelectedBallDrawTask(logic.getBoardWithNextBalls(), from));
                    }
                } else if (from != null) {
                    if (isTeleportMode) {
                        logic.makeTeleportation(from, location);
                        updateTeleportMode(false);
                    } else
                        logic.makeMove(from, location);
                    from = null;
                }
            }

            @Override
            public void onDrainAnimationTask() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateScores();
                    }
                });
            }
        });
        {
            View view = getFragmentView();
            // action buttons
            ImageButton btnUndo = view.findViewById(R.id.buttonUndo);
            btnUndo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logic.popUndo();
                }
            });
            ImageButton btnTeleport = view.findViewById(R.id.buttonTeleport);
            btnTeleport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateTeleportMode(!isTeleportMode);
                }
            });
            ImageButton btnMenu = view.findViewById(R.id.buttonMenu);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onMenuSelected();
                }
            });
        }
    }

    public void onButtonModeClick(View view) {
        int mode = 0;
        int id = view.getId();
        if (id == R.id.buttonRings)
            mode = 1;
        else if (id == R.id.buttonSquares)
            mode = 2;
        int[] strResource = {
                R.string.dialog_new_lines,
                R.string.dialog_new_rings,
                R.string.dialog_new_squares
        };
        ConfirmDialog confirmDialog = ConfirmDialog.getInstance(getString(strResource[mode]));
        confirmDialog.setNumericTag(mode);
        confirmDialog.setListener(getConfirmListener());
        BaseDialog.showDialog(confirmDialog, getFragmentManager());
    }

    private void updateTeleportMode(boolean value) {
        ImageButton btnTeleport = getFragmentView().findViewById(R.id.buttonTeleport);
        isTeleportMode = value;
        btnTeleport.setSelected(isTeleportMode);
    }

    private void setGameMode(long mode) {
        Preferences preferences = new Preferences(getActivity());
        preferences.putLong(GAME_MODE, mode);
        setupGameLogic();
        updateTeleportMode(isTeleportMode);
    }

    private int getBestScores() {
        Context context = getActivity();
        if ( context == null )
            return 0;
        Preferences preferences = new Preferences(context);
        long mode = preferences.getLong(GAME_MODE, 0);
        String key = BEST_SCORES + mode;
        return preferences.getInt(key, 0);
    }

    private void setBestScores(int value) {
        Context context = getActivity();
        if ( context == null )
            return;
        Preferences preferences = new Preferences(getActivity());
        long mode = preferences.getLong(GAME_MODE, 0);
        String key = BEST_SCORES + mode;
        preferences.putInt(key, value);
    }

    private void setupGameLogic() {
        final BoardView boardView = getGameBoardView();
        final Preferences preferences = new Preferences(getActivity());
        long mode = preferences.getLong(GAME_MODE, 0);
        if (mode == 1)
            logic = RingsLogic.getInstance();
        else if (mode == 2)
            logic = SquaresLogic.getInstance();
        else
            logic = LinesLogic.getInstance();
        {
            ImageView imageView = (ImageView) getFragmentView().findViewById(R.id.gameModeImage);
            int id;
            if (mode == 1)
                id = R.drawable.mode_ring;
            else if (mode == 2)
                id = R.drawable.mode_square;
            else
                id = R.drawable.mode_line;
            imageView.setImageResource(id);
        }
        logic.setDelegate(new BaseLogicDelegate() {
            @Override
            public void onGameBoardChanged(int[][] board) {
                invalidateBoard();
            }

            @Override
            public void onGameOver() {
                int bestScores = getBestScores();
                int scores = logic.getScores();
                boolean isWin = scores > bestScores;
                if (isWin) {
                    setBestScores(scores);
                }
                GameOverDialog dialog = GameOverDialog.getInstance(isWin, logic.getScores());
                dialog.setListener(getGameOverListener());
                BaseDialog.showDialog(dialog, getFragmentManager());
                if (listener != null)
                    listener.onGameOver();
            }

            @Override
            public void onWayNotExists() {
                int resId =  logic.getTeleportationCount() == 0 ? R.string.no_way : R.string.no_way_teleport;
                Toast.makeText(getActivity(), getString(resId), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScoresUpdated(int value) {
                // Do nothing
                // updateScores();
            }

            @Override
            public void onTeleportCountUpdated(int value) {
                ImageButton btnTeleport = getFragmentView().findViewById(R.id.buttonTeleport);
                btnTeleport.setEnabled(value > 0);
            }

            @Override
            public void onTeleportation(int[][] board, Location from, Location to) {
                boardView.addAnimationTask(new TeleportDrawTask(board, from, to));
            }

            @Override
            public void onWayDidFound(int[][] board, List<Location> traces) {
                boardView.addAnimationTask(new BallTraceDrawTask(board, traces));
            }

            @Override
            public void onBallsDisappear(int[][] board, int[][] markedBoard, Location from) {
                boardView.addAnimationTask(new DisappearDrawTask(board, markedBoard, from));
            }

            @Override
            public void onPutNextBalls(int[][] board) {
                boardView.addAnimationTask(new AppearDrawTask(board));
            }

            @Override
            public void onUndoStateChanged(boolean isAllowed) {
                ImageButton btnUndo = getFragmentView().findViewById(R.id.buttonUndo);
                btnUndo.setEnabled(isAllowed);
                invalidateBoard();
            }
        });
        logic.reset();
        updateBoardSize();
        isTeleportMode = false;
        from = null;
        if (listener != null)
            listener.onGameStarted(mode);
    }

    private void updateBoardSize() {
        BoardView boardView = getGameBoardView();
        boardView.setBoardSize(logic.getBoardSize());
        boardView.requestLayout();
        boardView.invalidate();
    }

    @Override
    public void onResume(Preferences preferences) {
        super.onResume(preferences);
        if (preferences.loadCoding(GAME_BOARD, logic)) {
            updateBoardSize();
            updateScores();
            this.from = preferences.getLocation(LOCATION_FROM);
            this.isTeleportMode = preferences.getBoolean(TELEPORT_MODE, false);
            if (this.from != null) {
                getGameBoardView().addAnimationTask(new SelectedBallDrawTask(logic.getBoardWithNextBalls(), from));
            } else {
                invalidateBoard();
            }
        } else {
            invalidateBoard();
        }
    }

    private void updateScores() {
        int value = logic.getScores();
        View view = getFragmentView();
        {
            TextView scores = view.findViewById(R.id.scoresText);
            scores.setText(String.valueOf(value));
        }
        {
            int bestScores = getBestScores();
            TextView textView = view.findViewById(R.id.bestScoresText);
            textView.setText(String.valueOf(bestScores));
        }
    }

    private void invalidateBoard() {
        getGameBoardView().addAnimationTask(new StaticBoardDrawTask(logic.getBoardWithNextBalls()));
    }


    @Override
    public void onPause(Preferences preferences) {
        super.onPause(preferences);
        preferences.saveCoding(GAME_BOARD, logic);
        preferences.saveCoding(LOCATION_FROM, from);
        preferences.putBoolean(TELEPORT_MODE, isTeleportMode);
    }

    private BoardView getGameBoardView() {
        return (BoardView) getFragmentView().findViewById(R.id.gameBoard);
    }

    public GameEventListener getListener() {
        return listener;
    }

    public void setListener(GameEventListener listener) {
        this.listener = listener;
    }
}
