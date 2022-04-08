package com.suisrc.kratos.eventbus.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadService {
    private ThreadService(){}
    
    private static ScheduledExecutorService service;

    public static ScheduledExecutorService getService() {
        if (service == null) {
            service = Executors.newScheduledThreadPool(2);
        }
        return service;
    }
}
