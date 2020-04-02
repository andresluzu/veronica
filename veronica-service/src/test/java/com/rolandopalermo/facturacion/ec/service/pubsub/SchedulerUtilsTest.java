package com.rolandopalermo.facturacion.ec.service.pubsub;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class SchedulerUtilsTest {

    private static final Date DATE = new Date();
    private static final int TIME_FACTOR = 5;
    private static final int RETRIES = 0;

    @Test
    public void addDelayZero() {
        Date result = SchedulerUtils.addDelay(DATE, RETRIES, TIME_FACTOR);
        Date date = Date.from(DATE.toInstant().plusMillis(5));
        assertEquals(result, date);
    }

    @Test
    public void addDelayOne() {
        Date result = SchedulerUtils.addDelay(DATE, 1, TIME_FACTOR);
        Date date = Date.from(DATE.toInstant().plusMillis(10));
        assertEquals(result, date);
    }

    @Test
    public void addDelayTwo() {
        Date result = SchedulerUtils.addDelay(DATE, 2, TIME_FACTOR);
        Date date = Date.from(DATE.toInstant().plusMillis(20));
        assertEquals(result, date);
    }

    @Test
    public void addDelayThree() {
        Date result = SchedulerUtils.addDelay(DATE, 3, TIME_FACTOR);
        Date date = Date.from(DATE.toInstant().plusMillis(40));
        assertEquals(result, date);
    }

    @Test
    public void addDelayFour() {
        Date result = SchedulerUtils.addDelay(DATE, 4, TIME_FACTOR);
        Date date = Date.from(DATE.toInstant().plusMillis(80));
        assertEquals(result, date);
    }

    @Test
    public void addDelayFive() {
        Date result = SchedulerUtils.addDelay(DATE, 5, TIME_FACTOR);
        Date date = Date.from(DATE.toInstant().plusMillis(160));
        assertEquals(result, date);
    }
}