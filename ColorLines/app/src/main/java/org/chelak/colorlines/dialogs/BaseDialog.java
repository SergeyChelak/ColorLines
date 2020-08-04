package org.chelak.colorlines.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Sergey on 07.03.2016.
 */
public abstract class BaseDialog extends DialogFragment {

    public static void showDialog(BaseDialog dialog, FragmentManager fragmentManager) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null) {
            return;
            //ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialog.show(ft, "dialog");
    }

    private int numericTag;

    protected abstract int getLayoutId();

    protected abstract void setupView(View view);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if ( savedInstanceState != null )
            numericTag = savedInstanceState.getInt("tag");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(getLayoutId(), null);
        setupView(view);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tag", numericTag);
    }

    public int getNumericTag() {
        return numericTag;
    }

    public void setNumericTag(int numericTag) {
        this.numericTag = numericTag;
    }
}
