package com.suisrc.kratos.eventbus.service;

import java.lang.reflect.InvocationTargetException;

public interface Actuator extends java.lang.Runnable {
    
    default void run() {
        try {
            run0();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    void run0() throws IllegalAccessException, InvocationTargetException;
}
