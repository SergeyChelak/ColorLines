package org.chelak.colorlines.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.chelak.colorlines.R;

/**
 * Created by Sergey on 07.03.2016.
 */
public class ConfirmDialog extends BaseDialog {

    public interface ConfirmDialogListener {
        void onSelectedPositive(ConfirmDialog dialog);

        void onSelectedNegative(ConfirmDialog dialog);
    }

    private final static String KEY_MESSAGE = "message";

    public static ConfirmDialog getInstance(String message) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString(KEY_MESSAGE, message);
        dialog.setArguments(args);
        return dialog;
    }

    private ConfirmDialogListener listener;
    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = getArguments().getString(KEY_MESSAGE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.confirm_dialog;
    }

    @Override
    protected void setupView(View view) {
        TextView txt = (TextView) view.findViewById(R.id.textMessage);
        //Typeface font = Typeface.createFromAsset(....getAssets(), "all_yoko.ttf");
        //txt.setTypeface(font);
        txt.setText(message);

        ImageButton btnYes = (ImageButton) view.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onSelectedPositive(ConfirmDialog.this);
                dismiss();
            }
        });

        ImageButton btnNo = (ImageButton) view.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onSelectedNegative(ConfirmDialog.this);
                dismiss();
            }
        });
    }

    public void setListener(ConfirmDialogListener listener) {
        this.listener = listener;
    }
}
