package com.lipakov.smartlink.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.lipakov.smartlink.R;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;
import com.lipakov.smartlink.service.SmartLinkApiService;
import com.lipakov.smartlink.service.SmartLinkFinderService;
import com.lipakov.smartlink.service.api.Crud;
import com.lipakov.smartlink.service.api.CrudApi;
import com.lipakov.smartlink.service.api.RestApi;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class SmartLinkPresenter implements CrudApi {
    private static final String TAG = SmartLinkPresenter.class.getSimpleName();
    private final Context context;
    private final SmartLinkView smartLinkView;
    private SmartLink smartLink;
    private final SmartLinkApiService smartLinkApiService;
    private final SharedPreferences sharedPreferences;
    public SmartLinkPresenter(final Context context, final SmartLinkView smartLinkView) {
        this.context = context;
        this.smartLinkView = smartLinkView;
        smartLink = new SmartLink();
        smartLinkApiService = new SmartLinkApiService(context, this);
        sharedPreferences = context.getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
    }
    @SuppressLint("CheckResult")
    public void addSmartLink(String urlOfLink) {
        Observable.create((ObservableOnSubscribe<SmartLink>) emitter -> {
                    SmartLinkFinderService smartLinkFinderService = new SmartLinkFinderService(context);
                    smartLink = smartLinkFinderService.findSmartLink(urlOfLink);
                    checkUrl(emitter);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> smartLinkView.showNotify(smartLink.getMessage()))
                .subscribe(result -> {}, error -> smartLinkView.showNotify(context.getString(R.string.error_of_connection)));
    }
    @SuppressLint("CheckResult")
    public void deleteSmartLink(SmartLink smartLink) {
        this.smartLink = smartLink;
        Observable.create((ObservableOnSubscribe<SmartLink>) emitter ->
                        smartLinkApiService.callResponse(emitter, Crud.DELETE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> smartLinkView.showNotify(context.getString(R.string.delete_link)))
                .subscribe(result -> {}, error -> smartLinkView.showNotify(context.getString(R.string.error_of_connection)));
    }
    @SuppressLint("CheckResult")
    public void deleteSmartLinkList() {
        Observable.create((ObservableOnSubscribe<SmartLink>) emitter -> smartLinkApiService.callResponse(emitter, Crud.DELETE_ALL))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> smartLinkView.showNotify(context.getString(R.string.delete_all_links)))
                .subscribe(result -> {}, error -> smartLinkView.showNotify(context.getString(R.string.error_of_connection)));
    }
    private void checkUrl(Emitter emitter) {
        String title = smartLink.getTitle();
        if (title == null) {
            smartLink.setMessage(context.getString(R.string.unknown_url));
            emitter.onComplete();
        } else {
            completeRequest(emitter);
        }
    }
    private void completeRequest(Emitter emitter) {
        UserSl userSl = getUserSlFromJson();
        smartLink.setUserSl(userSl);
        smartLink.setMessage("");
        smartLinkApiService.callResponse(emitter, Crud.CREATE);
    }

    private UserSl getUserSlFromJson() {
        String jsonUserSl = sharedPreferences.getString("usersl", "");
        return new Gson().fromJson(jsonUserSl, UserSl.class);
    }
    @Override
    public Call<ResponseBody> crudData(RestApi restApi, Crud crud) {
        if (crud.equals(Crud.CREATE)) {
            return restApi.addSmartLink(smartLink);
        } else if (crud.equals(Crud.DELETE)) {
            return restApi.deleteSmartLink(smartLink);
        } else if (crud.equals(Crud.DELETE_ALL)) {
            return restApi.deleteAll();
        }
        throw new IllegalArgumentException();
    }
    public interface SmartLinkView {
        void showNotify(String notify);
    }
}
