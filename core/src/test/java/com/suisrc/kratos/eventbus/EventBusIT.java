package com.suisrc.kratos.eventbus;

import java.lang.reflect.Method;

import com.suisrc.kratos.core.annotation.Subscribe;
import com.suisrc.kratos.eventbus.service.EventBusService;

import org.junit.Test;

public class EventBusIT {

    @Test
    public void t0001() throws NoSuchMethodException, SecurityException {
        System.out.println("running");
        EventBusService bus = new EventBusService();

        Handler object = new Handler();
        for (Method method : object.getClass().getMethods()) {
            Subscribe anno = method.getAnnotation(Subscribe.class);
            if (anno != null) {
                bus.subscribe("topic", "", object, method, anno);
            }

        }

        bus.publish("topic", "123", 1, 2, 3, "");

        // object.subscribe("123");
        // org.junit.Assert.assertTrue(false);
        System.out.println("end");
    }

    public static class Handler {
        @Subscribe
        public void subscribe(String key, Object ...params) {
            System.out.println(params.length);
        }
    }

    @Test
    public void t0002() {
        t0003(1, 2, 3);
    }

    public void t0003(int ...args) {
        Class<?> clazz = args.getClass();
        System.out.println(clazz.getComponentType());
    }
}
