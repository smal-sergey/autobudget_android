package com.smalser.autobudget;

public enum Category {
    QIWI,
    PRODUCTS,
    CASH_WITHDRAW,
    SPORT,
    //    CAFE,
//    CLOTHES,
    OTHER;

    /**
     * @return set of patterns
     */
    public String resolve() {
        String result;
        switch (this) {
            case QIWI:
                result = "(W.QIWI.RU)|(Oplata scheta)"; //todo Oplata scheta ?
                break;
            case PRODUCTS:
                result = "(OKEY)|(.*AUCHAN.*)|(MIRATORG.*)|(.*PEREKRESTOK.*)|(KOFEYNAYA KANTA)";
                break;
            case CASH_WITHDRAW:
                result = "(Snyatiye nalichnykh)|(Snjatie nalichnyh)";
                break;
            case SPORT:
                result = "(.*SPORTMASTER.*)";
                break;
//            case CAFE:
//                result = "()";
//                break;
//            case CLOTHES:
//                result = "()";
//                break;
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
