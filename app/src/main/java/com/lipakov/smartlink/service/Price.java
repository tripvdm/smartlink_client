package com.lipakov.smartlink.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
            Element elem = elements.get(0);
            return elem.text();
        }
        return "";
    }

    /*TODO Nlp searcher*/
    class NlpSearcher {

    }
}
