package com.suisrc.kratos.eventbus.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadService {
    private ThreadService(){}
    
    private static ExecutorService service;

    public static ExecutorService getService() {
        if (service == null) {
            service = Executors.newCachedThreadPool();
        }
        return service;
    }
}
