package com.lipakov.smartlink.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lipakov.smartlink.presenter.SmartLinkPresenter;

public class AddingOfSmartLinkListener implements SmartLinkPresenter.SmartLinkView {
    private final Context context;
    private final EditText editText;
    private final ProgressDialog progressDialog;
    private final AlertDialog alertDialog;

    public AddingOfSmartLinkListener(Context context, EditText editText,
                                     ProgressDialog progressDialog,
                                     AlertDialog alertDialog) {
        this.context = context;
        this.editText = editText;
        this.progressDialog = progressDialog;
        this.alertDialog = alertDialog;
    }

    public void onClick() {
        SmartLinkPresenter smartLinkPresenter = new SmartLinkPresenter(context, this);
        smartLinkPresenter.addSmartLink(editText.toString());
    }

    @Override
    public void showNotify(String notify) {
        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
        alertDialog.show();
        progressDialog.dismiss();
    }

}