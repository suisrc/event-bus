package com.suisrc.kratos.eventbus.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// 多线程服务， 具体业务中应该重新EventBusService中的内容，再AbstractEventBusManager中替换
class ThreadService {
    private ThreadService(){}
    private static ScheduledExecutorService service;

    static ScheduledExecutorService getService() {
        if (service == null) {
            service = Executors.newScheduledThreadPool(2);
        }
        return service;
    }
}
