package com.openfaas.function.common;

import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

public class SessionToken {
	public String session;
    public String proprietaryLocation;
    public String currentLocation;

    public void init() {
        session = System.getenv("LOCATION_ID") + "_" +
                Calendar.getInstance().getTimeInMillis() + "_" +
                ThreadLocalRandom.current().nextInt();
        proprietaryLocation = System.getenv("LOCATION_ID");
        currentLocation = System.getenv("LOCATION_ID");
    }
}
