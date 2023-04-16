package com.lipakov.smartlink.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.widget.Toast;

import com.lipakov.smartlink.presenter.SmartLinkPresenter;

public class AddingOfSmartLinkListener implements DialogInterface.OnClickListener, SmartLinkPresenter.SmartLinkView {
    private final Context context;
    private final Editable editable;

    public AddingOfSmartLinkListener(Context context, Editable editable) {
        this.context = context;
        this.editable = editable;
    }

    @Override
    public void onClick(android.content.DialogInterface dialog, int which) {
        SmartLinkPresenter smartLinkPresenter = new SmartLinkPresenter(this);
        smartLinkPresenter.addSmartLink(editable.toString());
    }

    @Override
    public void showNotify(String notify) {
        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
    }
}