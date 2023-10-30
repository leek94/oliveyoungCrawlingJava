package util;

public class common {

    public static double calculateDiscountPercent(String bePrice, String price) {

        int bePriceInt = Integer.parseInt(bePrice);
        int priceInt = Integer.parseInt(price);
        return ((bePriceInt - priceInt) / (double) bePriceInt * 100);
    }
}
