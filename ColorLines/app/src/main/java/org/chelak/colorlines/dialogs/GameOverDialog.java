package org.chelak.colorlines.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.chelak.colorlines.R;

/**
 * Created by Sergey on 06.03.2016.
 */
public class GameOverDialog extends BaseDialog {

    public interface GameOverDialogListener {
        void onNewGame();
    }

    private final static String KEY_IS_WIN = "is.win";
    private final static String KEY_SCORES = "scores";

    public static GameOverDialog getInstance(boolean isWin, int scores) {
        GameOverDialog dialog = new GameOverDialog();
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_WIN, isWin);
        args.putInt(KEY_SCORES, scores);
        dialog.setArguments(args);
        return dialog;
    }

    private boolean isWin;
    private int scores;
    private GameOverDialogListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.isWin = args.getBoolean(KEY_IS_WIN);
        this.scores = args.getInt(KEY_SCORES);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.game_over_dialog;
    }

    @Override
    protected void setupView(View view) {
        TextView textView = (TextView) view.findViewById(R.id.textScores);
        textView.setText(String.valueOf(scores));

        ImageView imageView = (ImageView) view.findViewById(R.id.imageTitle);
        imageView.setImageResource(isWin ? R.drawable.logo_best : R.drawable.logo_normal);

        ImageButton btnRestart = (ImageButton) view.findViewById(R.id.btnNewGame);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameOverDialog.this.dismiss();
            }
        });
/*
        ImageView btnFacebook = (ImageButton) view.findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameOverDialog.this.dismiss();
                openFacebookPublic(getActivity());
            }
        });
*/
    }

    public void setListener(GameOverDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void dismiss() {
        if (listener != null)
            listener.onNewGame();
        super.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        GameOverDialog.this.dismiss();
    }

    public static void openFacebookPublic(Context context) {
        String url = context.getString(R.string.url_public_facebook);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}
