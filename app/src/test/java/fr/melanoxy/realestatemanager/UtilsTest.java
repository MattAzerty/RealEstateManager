package fr.melanoxy.realestatemanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    void getTodayDate() {//Use of another lib for the test
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateResult = LocalDateTime.now().format(formatter);
        Assertions.assertNotNull(Utils.getTodayDate());
        Assertions.assertEquals(dateResult,Utils.getTodayDate());
    }
}