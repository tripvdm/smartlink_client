package com.lipakov.smartlink.service;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Photo implements FinderOfValue {

    @Override
    public String findElement(Document document) {
        Elements metaOgImage = document.select("meta[property=og:image]");
        return metaOgImage.attr("content");
    }

}
