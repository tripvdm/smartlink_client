package com.lipakov.smartlink.service;

import android.content.Context;
import android.util.Log;

import com.lipakov.smartlink.R;
import com.lipakov.smartlink.model.SmartLink;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SmartLinkFinderService {
    private static final String TAG = SmartLinkFinderService.class.getSimpleName();

    private final FinderOfValue photoFinder;
    private final FinderOfValue titleFinder;
    private final FinderOfValue priceFinder;
    private final FinderOfValue phoneNumberFinder;

    private Context context;
    public SmartLinkFinderService(Context context) {
        photoFinder = new Photo();
        titleFinder = new Title();
        priceFinder = new Price();
        phoneNumberFinder = new PhoneNumber();
        this.context = context;
    }

    public SmartLink findSmartLink(String urlOfLink) {
        return getSmartLink(urlOfLink);
    }

    private SmartLink getSmartLink(String url) {
        SmartLink smartLink = new SmartLink();
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(5000).get();
            smartLink.setPhoto(photoFinder.findElement(doc));
            smartLink.setTitle(titleFinder.findElement(doc));
            smartLink.setPrice(priceFinder.findElement(doc));
            smartLink.setPhoneNumber(phoneNumberFinder.findElement(doc));
            smartLink.setUrl(url);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            smartLink.setMessage(context.getString(R.string.error_of_connection));
        }
        return smartLink;
    }

}
