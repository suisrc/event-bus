package com.suisrc.kratos.eventbus.service;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import com.suisrc.kratos.core.annotation.Subscribe;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @see Data
 * @see NonNull
 * @author Y13
 *
 */
// @Data
@RequiredArgsConstructor
public class Handler implements Comparable<Handler> {

    // 线程标识
    @Getter
    private final String thread;
    // 订阅实体
    @Getter
    private final Object object;
    // 订阅方法
    @Getter
    private final Method method;
    // 单次执行
    @Getter
    private final boolean once;
    // 同步执行
    @Getter
    private final boolean sync;
    // 订阅注解
    @Getter
    private final Subscribe annotation;

    // 方法参数
    private Class<?>[] parameters;

    // 锁
    // private final Lock lock = new ReentrantLock()

    public Handler init() {
        parameters = method.getParameterTypes();
        return this;
    }

    public Actuator exec(Object... args) {
        if (parameters.length == 0) {
            return () -> method.invoke(object);
        }
        Class<?> varArgsType = Void.class;
        Object varArgs = null;
        int varArgsIdx = parameters.length;
        if (method.isVarArgs()) {
            if (parameters.length - 1 > args.length) {
                return null; // 缺少参数， 禁止调用
            }
            varArgsIdx -= 1;
            varArgsType = parameters[varArgsIdx].getComponentType();
            varArgs = Array.newInstance(varArgsType, args.length - varArgsIdx);
        } else if (args.length != parameters.length) {
            return null; // 不存在可变参数，参数数量不相等
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue; // do nothing
            }
            if (i < varArgsIdx && !parameters[i].isAssignableFrom(args[i].getClass())) {
                return null;
            }
            if (i >= varArgsIdx && !varArgsType.isAssignableFrom(args[i].getClass())) {
                return null;
            }
        }
        if (varArgs != null) {
            if (args.length > varArgsIdx) {
                System.arraycopy(args, varArgsIdx, varArgs, 0, args.length - varArgsIdx);
            }
            Object[] params = new Object[varArgsIdx + 1];
            System.arraycopy(args, 0, params, 0, varArgsIdx);
            params[varArgsIdx] = varArgs;
            return () -> method.invoke(object, params);
        } else {
            return () -> method.invoke(object, args);
        }
    }

    @Override
    public int compareTo(Handler that) {
        return Integer.compare(this.annotation.order(), that.annotation.order());
    }
}
