package fr.melanoxy.realestatemanager.ui.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtilsTest {

    @Test
    void convertDollarToEuro() {
        assertEquals(81, Utils.convertDollarToEuro(100));
        assertEquals(0, Utils.convertDollarToEuro(0));
        assertEquals(-81, Utils.convertDollarToEuro(-100));
    }

    @Test
    void convertEuroToDollar() {
        assertEquals(120, Utils.convertEuroToDollar(100));
        assertEquals(0, Utils.convertEuroToDollar(0));
        assertEquals(-120, Utils.convertEuroToDollar(-100));
    }

    @Test
    void getTodayDate() {
        assertNotNull(Utils.getTodayDate());
    }
}