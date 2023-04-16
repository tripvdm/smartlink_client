package com.lipakov.smartlink.presenter;

import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.service.SmartLinkApiService;
import com.lipakov.smartlink.service.SmartLinkService;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SmartLinkPresenter {
    private static final String TAG = SmartLinkPresenter.class.getSimpleName();

    private final SmartLinkView smartLinkView;
    private SmartLink smartLink;
    private final SmartLinkApiService smartLinkApiService;

    public SmartLinkPresenter(final SmartLinkView smartLinkView) {
        this.smartLinkView = smartLinkView;
        smartLink = new SmartLink();
        smartLinkApiService = new SmartLinkApiService();
    }
    
    public void addSmartLink(String urlOfLink) {
        Observable.create((ObservableOnSubscribe<SmartLink>) emitter -> {
                    SmartLinkService nlpSmartLinkService = new SmartLinkService();
                    smartLink = nlpSmartLinkService.findSmartLink(urlOfLink);
                    smartLinkApiService.callResponse(emitter, smartLink);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> smartLinkView.showNotify(smartLink.getMessage()))
                .subscribe();
    }

    public interface SmartLinkView {
        void showNotify(String notify);
    }
}
