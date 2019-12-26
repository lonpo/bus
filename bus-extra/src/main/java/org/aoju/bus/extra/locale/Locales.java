package org.aoju.bus.extra.locale;

/**
 * @author Kimi Liu
 * @version 5.3.8
 * @since JDK 1.8+
 */
public interface Locales {

    void init(Observer observer);

    boolean doIt(byte[] aBuf, int aLen, boolean oDontFeedMe);

}

