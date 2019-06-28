package com.total.unique.generator;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestBeerAlgorithm {

    @Test
    public void test() {
        BeearDecimalGenerator.from(62);
    }

    public static class BeearDecimalGenerator {


        private static final String VALUES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public static  void from(int year) {

            int current = year;
            log.info("Current={}", current);
            int length = VALUES.length();
            int reste = current%length;
            int quotient = current/length;

            String result = VALUES.substring(reste, reste+1);

            log.info("quotient={}", quotient);

            boolean greaterThan2 = false;
            while(quotient >= length) {
                greaterThan2 = true;
                current = quotient;
                reste = current%length;
                quotient = current/length;

                log.info("reste={}", reste);
                log.info("quotient={}", quotient);

                result = VALUES.substring(quotient, quotient + 1) +  VALUES.substring(reste, reste+1) + result;
            }

            if (!greaterThan2) {
                result = VALUES.substring(quotient, quotient + 1) +  VALUES.substring(reste, reste+1);
            }

            //String result = VALUES.substring(quotient, quotient + 1) + VALUES.substring(reste, reste+1);
            log.info("year={} gives {}", year, result);
        }
    }
}
