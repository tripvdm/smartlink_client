package com.lipakov.smartlink.utils;

import android.app.ProgressDialog;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lipakov.smartlink.MainActivity;

public class UtilsUI {
    public static EditText createEditText(MainActivity activity) {
        final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        return input;
    }

    public static ProgressDialog createProgressDialog(MainActivity activity) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("EXPECTING...");
        return progressDialog;
    }

}
