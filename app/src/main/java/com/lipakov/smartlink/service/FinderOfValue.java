package com.lipakov.smartlink.service;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface FinderOfValue {
    String findElement(Document document) throws IOException;
}
