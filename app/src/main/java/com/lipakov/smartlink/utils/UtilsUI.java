package com.lipakov.smartlink.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lipakov.smartlink.R;

/*TODO Singleton*/
public class UtilsUI {
    public static EditText createEditText(Context context) {
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        return input;
    }

    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("EXPECTING...");
        return progressDialog;
    }

    public static void setTextColor(String notify, TextView toastMessage) {
        if (!notify.isBlank()) {
            toastMessage.setTextColor(Color.RED);
            toastMessage.setText(notify);
        } else {
            toastMessage.setTextColor(Color.GREEN);
            toastMessage.setText(R.string.success_url_added);
        }
    }
}
