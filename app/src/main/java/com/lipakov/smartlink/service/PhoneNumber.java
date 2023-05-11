package com.lipakov.smartlink.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

public class PhoneNumber implements FinderOfValue {
    @Override
    public String findElement(Document document) {
        for (Element element : document.getElementsByAttribute("class")) {
            String phoneNumber = findPhoneNumber(element);
            if (!phoneNumber.isBlank()) return phoneNumber;
        }
        return "";
    }

    private String findPhoneNumber(Element element) {
        String className = element.className();
        if (className.contains("phone") || className.contains("Phone")) {
            Elements elements = element.getElementsByClass(element.className());
            String text = elements.text();
            String[] tokens = text.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            return findPhoneNumber(tokens);
        }
        return "";
    }

    private String findPhoneNumber(String[] tokens) {
        for (String token : tokens) {
            if (isPhoneNumber(token)) {
                return token;
            }
        }
        return "";
    }

    private boolean isPhoneNumber(String word) {
        String patterns
                = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
        Pattern pattern = Pattern.compile(patterns);
        return pattern.matcher(word).matches();
    }
}
