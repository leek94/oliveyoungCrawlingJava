package util;

import org.jsoup.nodes.Element;

import java.util.Optional;

public class common {

    public static double calculateDiscountPercent(String bePrice, String price) {
        if(bePrice.equals("0")){
            return 0.0;

        }

        int bePriceInt = Integer.parseInt(bePrice);
        int priceInt = Integer.parseInt(price);
        return ((bePriceInt - priceInt) / (double) bePriceInt * 100);
    }

    public static String nullCheck(Element element) {

        return Optional.ofNullable(element)
                .map(Element::text)
                .orElse("");
    }

    public static String nullCheckPrice(Element element) {

        return Optional.ofNullable(element)
                .map(Element::text)
                .map(text -> text.replaceAll("[^0-9]", ""))
                .orElse("0");
    }
}
