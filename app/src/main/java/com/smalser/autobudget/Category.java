package com.smalser.autobudget;

public enum Category {
    QIWI,
    PRODUCTS,
    CASH_WITHDRAW,
    SPORT,
    OTHER;

    /**
     * @return set of patterns
     */
    public String resolve() {
        String result;
        switch (this) {
            case QIWI:
                result = "W.QIWI.RU";
                break;
            case PRODUCTS:
                result = "(OKEY)|(.*AUCHAN.*)|(MIRATORG.*)";
                break;
            case CASH_WITHDRAW:
                result = "Snyatiye nalichnykh";
                break;
            case SPORT:
                result = "(.*SPORTMASTER.*)";
                break;
            case OTHER:
                result = ".*";
                break;
            default:
                result = ".*";
                break;
        }
        return result;
    }
}
