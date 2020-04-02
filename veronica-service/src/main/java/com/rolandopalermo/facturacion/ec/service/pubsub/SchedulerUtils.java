package com.rolandopalermo.facturacion.ec.service.pubsub;

import java.util.Date;

public class SchedulerUtils {

    public static Date addDelay(Date date, int retries, int timeFactor) {
        double delay = (Math.pow(2, retries) * timeFactor);
        return Date.from(date.toInstant().plusMillis((int) delay));
    }
}
