package com.lipakov.smartlink.presenter;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;
import com.lipakov.smartlink.service.api.InsertApi;
import com.lipakov.smartlink.service.SmartLinkApiService;
import com.lipakov.smartlink.service.api.SmartLinkApi;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class UserPresenter implements InsertApi {
    private static final String TAG = UserPresenter.class.getSimpleName();
    private final DisplayView displayView;
    private final SmartLinkApiService smartLinkApiService;
    UserSl userSl = new UserSl();
    public UserPresenter(Context context, DisplayView displayView) {
        this.displayView = displayView;
        smartLinkApiService = new SmartLinkApiService(context, this);
    }
    public void addUser(GoogleSignInAccount account) {
        Observable.create((ObservableOnSubscribe<SmartLink>) emitter -> {
            userSl.setLogin(account.getDisplayName());
            userSl.setEmail(account.getEmail());
            smartLinkApiService.callResponse(emitter);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> displayView.displayMainActivity(account.getDisplayName()))
                .subscribe();
    }

    @Override
    public Call<ResponseBody> addData(SmartLinkApi smartLinkApi) {
        return smartLinkApi.addUserSl(userSl);
    }

    public interface DisplayView {
        void displayMainActivity(String displayLogin);
    }
}
