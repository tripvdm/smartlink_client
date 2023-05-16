package com.lipakov.smartlink.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

public class Price implements FinderOfValue {
    @Override
    public String findElement(Document document) {
        for (Element element : document.getElementsByAttribute("class")) {
            String price = findPrice(element);
            if (!price.isBlank()) return price;
        }
        return "";
    }

    /**
     * <p>Вохварщает цену
     * (Берет значение класса элемента html и проверяет на "price")<p/>
     * @param element Элемент тега
     * @return текст classa с price, если находит, иначе "", если не находит
     * */
    private String findPrice(Element element) {
        String className = element.className();
        if (className.contains("price") || className.contains("Price")) {
            Elements elements = element.getElementsByClass(element.className());
            return findPrice(elements.text());
        }
        return "";
    }

    /**
     * <p>Проверяет на число и на валюту
     * (валюта должна быть одна а чисел может быть несколько)<p/>
     * @param text значение внутри html тега
     * @return текст classa с price, если находит, иначе "", если не находит
     * */
    private String findPrice(String text) {
        String[] tokens = text.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        int countWords = 0;
        boolean numeric = false;
        for (String token : tokens) {
            if (isNumeric(token)) {
                numeric = true;
            } else {
                countWords++;
            }
        }
        if (numeric && countWords == 1) return text;
        return "";
    }

    private boolean isNumeric(String word) {
        String regExp = "\\d+";
        Pattern pattern = Pattern.compile(regExp);
        return pattern.matcher(word).matches();
    }

}
