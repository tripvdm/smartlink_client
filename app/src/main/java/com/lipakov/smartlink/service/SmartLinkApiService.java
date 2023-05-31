package com.lipakov.smartlink.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lipakov.smartlink.R;
import com.lipakov.smartlink.service.api.InsertApi;
import com.lipakov.smartlink.service.api.RestApi;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartLinkApiService {
    private static final String TAG = SmartLinkApiService.class.getSimpleName();
    private final Context context;

    private final InsertApi insertApi;

    public SmartLinkApiService(Context context, InsertApi insertApi) {
        this.context = context;
        this.insertApi = insertApi;
    }

    public void callResponse(final Emitter emitter) {
        RestApi restApi = RetrofitService.getInterface();
        Call<ResponseBody> call = insertApi.addData(restApi);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.i(TAG, response.message());
                emitter.onComplete();
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                String stackTrace = Arrays.toString(t.getStackTrace());
                Log.e(TAG, stackTrace);
                emitter.onError(new Throwable(context.getString(R.string.error_of_connection)));
            }
        });
    }

    public OkHttpClient getUnsafeOkHttpClient() {
        try {
            @SuppressLint("CustomX509TrustManager")
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
            KeyStore keyStore = readKeyStore();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "smartlink.p12".toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagers, trustAllCerts, new SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .readTimeout(20, TimeUnit.SECONDS);
            return builder.build();
        } catch (final Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return new OkHttpClient();
        }
    }

    private KeyStore readKeyStore() {
        KeyStore ks = null;
        InputStream in;
        try {
            ks = KeyStore.getInstance("PKCS12");
            char[] password = "chinatown".toCharArray();
            in = context.getResources().openRawResource(R.raw.smartlink);
            ks.load(in, password);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return ks;
    }

}
