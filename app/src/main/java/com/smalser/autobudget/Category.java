package com.smalser.autobudget;

public enum Category {
    QIWI,
    PRODUCTS,
    CASH_WITHDRAW,
    SPORT,
    CAFE,
    TRANSPORT,
    CLOTHES,
    RELAX,
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
                result = "(O KEY)|(OKEY)|(.*AUCHAN.*)|(MIRATORG.*)|(.*PEREKRESTOK.*)|(KOFEYNAYA KANTA.*)|(LENTA)";
                break;
            case CASH_WITHDRAW:
                result = "(Snyatiye nalichnykh)|(Snjatie nalichnyh)";
                break;
            case SPORT:
                result = "(.*SPORTMASTER.*)|(.* KANT .*)";
                break;
            case CAFE:
                result = "(MUMU)|(ZELENAYA GORCHIORENBURG)|(.*KFC.*)|(.*MCDONALDS.*)|(.*Burger Club.*)|" +
                        "(TASHIR)|(THE PASHA)|(DUNKIN DONUTS.*)|(GEISHA.*)|(SBARRO)|(SHOKO.*)|(CAFETERA WHITE)|(KOFETUN)";
                break;
            case TRANSPORT:
                result = "(WWW.RZD.RU)|(.*ORENBURG AIRLI.*)|(AEROFLOT.*)|(UZ.GOV.UA)|(RAILWAYTICKETS KYIV)";
                break;
            case CLOTHES:
                result = "(DZHULIANNA)|(.*CALZEDONIA.*)|(COLINS.*)|(MANGO)|(RESERVED.*)|(YNG)|(ZOLLA)|(RALFRINGER)|(OSTIN)|(MOHITO TTS RIO)";
                break;
            case RELAX:
                result = "(.*CINEMA.*)|(WWW.KINOHOD.RU MOSCOW)|(KINOBAR)";
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
