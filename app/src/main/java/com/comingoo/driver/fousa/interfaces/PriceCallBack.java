package com.comingoo.driver.fousa.interfaces;

public interface PriceCallBack {
    void callbackPrice(Double att, Double base, Double debtCeil,
                       Double km, Double minimum, Double percent, boolean isPromoCode, double earn, double voyages, double debt);
}