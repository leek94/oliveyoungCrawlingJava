package util;

public class common {

    public static double calculateDiscountPercent(String bePrice, String price) {
        if(bePrice.equals("0")){
            return 0.0;

        }

        int bePriceInt = Integer.parseInt(bePrice);
        int priceInt = Integer.parseInt(price);
        return ((bePriceInt - priceInt) / (double) bePriceInt * 100);
    }

}
