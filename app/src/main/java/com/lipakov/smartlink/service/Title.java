package com.lipakov.smartlink.service;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Title implements FinderOfValue {
    @Override
    public String findElement(Document document) {
        Elements metaOgTitle = document.select("meta[property=og:title]");
        String attr = metaOgTitle.attr("content");
        if (!attr.isBlank()) {
            return attr;
        } else {
            return document.title();
        }
    }

}
