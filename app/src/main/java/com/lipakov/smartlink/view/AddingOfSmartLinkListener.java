package com.lipakov.smartlink.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lipakov.smartlink.R;
import com.lipakov.smartlink.presenter.SmartLinkPresenter;

public class AddingOfSmartLinkListener implements SmartLinkPresenter.SmartLinkView {
    private final Context context;
    private final EditText editText;
    private final ProgressDialog progressDialog;
    private final AlertDialog alertDialog;
    private final AddingOfSmartLink addingOfSmartLink;

    public AddingOfSmartLinkListener(Context context, AddingOfSmartLink addingOfSmartLink) {
        this.context = context;
        this.addingOfSmartLink = addingOfSmartLink;
        this.editText = addingOfSmartLink.getEditText();
        this.progressDialog = addingOfSmartLink.getProgressDialog();
        this.alertDialog = addingOfSmartLink.getAlertDialog();
    }

    public void onClick() {
        alertDialog.dismiss();
        progressDialog.show();
        SmartLinkPresenter smartLinkPresenter = new SmartLinkPresenter(context, this);
        smartLinkPresenter.addSmartLink(editText.getText().toString());
    }

    @Override
    public void showNotify(String notify) {
        Toast toast = Toast.makeText(context, notify, Toast.LENGTH_SHORT);
        TextView toastMessage = toast.getView().findViewById(android.R.id.message);
        setTextColor(notify, toastMessage);
        toast.show();
        alertDialog.show();
        progressDialog.dismiss();
        addingOfSmartLink.startObserverOfSmartLink();
    }

    private static void setTextColor(String notify, TextView toastMessage) {
        if (!notify.isBlank()) {
            toastMessage.setTextColor(Color.RED);
            toastMessage.setText(notify);
        } else {
            toastMessage.setTextColor(Color.GREEN);
            toastMessage.setText(R.string.success_url_added);
        }
    }

    public interface AddingOfSmartLink {
        EditText getEditText();
        AlertDialog getAlertDialog();
        ProgressDialog getProgressDialog();
        void startObserverOfSmartLink();
    }
}