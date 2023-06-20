package com.lipakov.smartlink.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;
import com.lipakov.smartlink.service.SmartLinkApiService;
import com.lipakov.smartlink.service.api.Crud;
import com.lipakov.smartlink.service.api.CrudApi;
import com.lipakov.smartlink.service.api.RestApi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class UserPresenter implements CrudApi {
    private static final String TAG = UserPresenter.class.getSimpleName();
    private final DisplayView displayView;
    private final SmartLinkApiService smartLinkApiService;
    private SharedPreferences sharedPreferences;
    private final UserSl userSl = new UserSl();
    public UserPresenter(Context context, DisplayView displayView) {
        this.displayView = displayView;
        smartLinkApiService = new SmartLinkApiService(context, this);
        sharedPreferences = context.getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
    }
    @SuppressLint("CommitPrefEdits")
    public Disposable addUser(GoogleSignInAccount account) {
        int threadCt = Runtime.getRuntime().availableProcessors() + 1;
        ExecutorService executor = Executors.newFixedThreadPool(threadCt);
        return Observable.create((ObservableOnSubscribe<SmartLink>) emitter -> {
                    putUserSl(account);
                    smartLinkApiService.callResponse(emitter, Crud.CREATE);
        }).subscribeOn(Schedulers.from(executor))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> displayView.displayMainActivity(account.getGivenName()))
                .subscribe();
    }

    private void putUserSl(GoogleSignInAccount account) {
        Gson gson = new Gson();
        userSl.setLogin(account.getDisplayName());
        userSl.setEmail(account.getEmail());
        String jsonText = gson.toJson(userSl);
        sharedPreferences.edit().putString("usersl", jsonText).apply();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Override
    public Call<ResponseBody> crudData(RestApi restApi, Crud crud) {
        return restApi.addUserSl(userSl);
    }

    public interface DisplayView {
        void displayMainActivity(String displayLogin);
    }
}
