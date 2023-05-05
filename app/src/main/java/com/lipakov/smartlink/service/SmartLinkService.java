package com.lipakov.smartlink.service;

import com.lipakov.smartlink.model.SmartLink;

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

    /**TODO находит нужные поля**/
    public SmartLink findSmartLink(String urlOfLink) {
        return getSmartLink(urlOfLink);
    }

    private SmartLink getSmartLink(String url) {
        SmartLink smartLink = new SmartLink();
        smartLink.setPhoto(photoFinder.findElement(url));
        smartLink.setTitle(titleFinder.findElement(url));
        smartLink.setPrice(priceFinder.findElement(url));
        smartLink.setUrl(phoneNumberFinder.findElement(url));
        return smartLink;
    }

}
