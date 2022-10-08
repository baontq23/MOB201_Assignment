package com.baontq.mob201.until;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baontq.mob201.R;

public class ProgressBarDialog {
    private View view;
    private TextView msg;
    private ProgressBar progressBar;
    private LinearLayout ll;
    private AlertDialog.Builder builder;
    private Dialog dialog;

    public ProgressBarDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_progress_dialog, null);
        init();
    }

    private void init() {
        msg = view.findViewById(R.id.msg);
        progressBar = (view.findViewById(R.id.loader));
        ll = view.findViewById(R.id.ll);
        builder = new AlertDialog.Builder(view.getContext());
    }

    public ProgressBarDialog setBackgroundColor(int color) {
        ll.setBackgroundColor(color);
        return this;
    }

    public ProgressBarDialog setProgressColor(int color) {
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
        return this;
    }

    public ProgressBarDialog setMessage(String message) {
        msg.setText(message);
        return this;
    }

    public ProgressBarDialog setMessageColor(int color) {
        msg.setTextColor(color);
        return this;
    }

    public void show() {
        builder.setView(view);
        if (dialog == null) {
            dialog = builder.create();
        }
        dialog.setCanceledOnTouchOutside(false);
        setBackgroundColor(Color.parseColor("#24242E"));
        setMessageColor(Color.parseColor("#FFFFFF"));
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

}
