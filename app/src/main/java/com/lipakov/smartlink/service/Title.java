package com.lipakov.smartlink.service;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Title implements FinderOfValue {
    @Override
    public String findElement(Document document) {
        Elements metaOgTitle = document.select("meta[property=og:title]");
        if (metaOgTitle != null) {
            return metaOgTitle.attr("content");
        } else {
            return document.title();
        }
    }

}
