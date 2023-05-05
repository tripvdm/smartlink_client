package com.lipakov.smartlink.presenter;

import android.content.Context;

import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;
import com.lipakov.smartlink.service.api.InsertApi;
import com.lipakov.smartlink.service.SmartLinkApiService;
import com.lipakov.smartlink.service.SmartLinkService;
import com.lipakov.smartlink.service.api.RestApi;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class SmartLinkPresenter implements InsertApi {
    private static final String TAG = SmartLinkPresenter.class.getSimpleName();

    private final SmartLinkView smartLinkView;
    private SmartLink smartLink;
    private final SmartLinkApiService smartLinkApiService;

    public SmartLinkPresenter(final Context context, final SmartLinkView smartLinkView) {
        this.smartLinkView = smartLinkView;
        smartLink = new SmartLink();
        smartLinkApiService = new SmartLinkApiService(context, this);
    }

    public void addSmartLink(String urlOfLink) {
        Observable.create((ObservableOnSubscribe<SmartLink>) emitter -> {
                    SmartLinkService nlpSmartLinkService = new SmartLinkService();
                    smartLink = nlpSmartLinkService.findSmartLink(urlOfLink);
                    smartLink.setUserSl(new UserSl());
                    smartLinkApiService.callResponse(emitter);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> smartLinkView.showNotify(smartLink.getMessage()))
                .subscribe();
    }

    @Override
    public Call<ResponseBody> addData(RestApi restApi) {
        return restApi.addSmartLink(smartLink);
    }

    public interface SmartLinkView {
        void showNotify(String notify);
    }
}
