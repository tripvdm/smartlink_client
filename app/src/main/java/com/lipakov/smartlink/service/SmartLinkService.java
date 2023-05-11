package com.lipakov.smartlink.service;

import android.util.Log;

import com.lipakov.smartlink.model.SmartLink;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SmartLinkService {
    private static final String TAG = SmartLinkService.class.getSimpleName();

    private final FinderOfValue photoFinder;
    private final FinderOfValue titleFinder;
    private final FinderOfValue priceFinder;
    private final FinderOfValue phoneNumberFinder;
    
    public SmartLinkService() {
        photoFinder = new Photo();
        titleFinder = new Title();
        priceFinder = new Price();
        phoneNumberFinder = new PhoneNumber();
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
        }
        return smartLink;
    }

}
