package fr.melanoxy.realestatemanager;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.melanoxy.realestatemanager.ui.utils.Utils;

class UtilsTest {

    @Test
    void convertDollarToEuro() {
        Assertions.assertEquals(81, Utils.convertDollarToEuro(100));
        Assertions.assertEquals(0, Utils.convertDollarToEuro(0));
        Assertions.assertEquals(-81, Utils.convertDollarToEuro(-100));
    }

    @Test
    void convertEuroToDollar() {
        Assertions.assertEquals(120, Utils.convertEuroToDollar(100));
        Assertions.assertEquals(0, Utils.convertEuroToDollar(0));
        Assertions.assertEquals(-120, Utils.convertEuroToDollar(-100));
    }

    @Test
    void getTodayDate() {
        Assertions.assertNotNull(Utils.getTodayDate());
    }
}