package com.suisrc.kratos.eventbus.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.suisrc.kratos.core.annotation.Subscribe;

/**
 * 
 * @author Y13
 *
 */
public class EventBusService {
    
    protected Map<String, Map<Object, Handler>> handlers = new ConcurrentHashMap<>(); // 线程安全
    
    // 锁
    // ReentrantReadWriteLock rwl = new ReentrantReadWriteLock()
    // Lock rlock = rwl.readLock()
    // Lock wlock = rwl.writeLock()

    /**
     * 注册
     * @param topic
     * @param method
     * @return
     */
    public boolean subscribe(String topic, String thread, Object object, Method method, Subscribe anno) {
        return doSubscribe(topic, new Handler(thread, object, method, false, true, anno).init());
    }

    /**
     * 注册
     * @param topic
     * @param method
     * @return
     */
    public boolean subscribeAsync(String topic, String thread, Object object, Method method, Subscribe anno) {
        return doSubscribe(topic, new Handler(thread, object, method, false, false, anno).init());
    }

    /**
     * 注册
     * @param topic
     * @param method
     * @return
     */
    public boolean subscribeOnce(String topic, String thread, Object object, Method method, Subscribe anno) {
        return doSubscribe(topic, new Handler(thread, object, method, true, true, anno).init());
    }

    /**
     * 注册
     * @param topic
     * @param method
     * @return
     */
    public boolean subscribeOnceAsync(String topic, String thread, Object object, Method method, Subscribe anno) {
        return doSubscribe(topic, new Handler(thread, object, method, true, false, anno).init());
    }

    /**
     * 查询注册
     * @param topic
     * @param method
     * @return
     */
    public boolean hasSubscribe(String topic) {
        Map<Object, Handler> map = handlers.get(topic);
        return map != null && !map.isEmpty();
    }

    /**
     * 获取所有的订阅
     * @return
     */
    public Set<String> getTopicAll() {
        return handlers.keySet();
    }

    /**
     * 取消注册
     * @param topic
     * @param method
     * @return
     */
    public boolean unsubscribe(String topic, Object method) {
        if (!(method instanceof Method)) {
            return false;
        }
        Map<Object, Handler> map = handlers.get(topic);
        if (map == null || map.isEmpty()) {
            return false;
        }
        Handler hdl = map.remove(method);
        if (hdl != null && map.isEmpty()) {
            handlers.remove(topic);
        }
        return hdl != null;
    }

    /**
     * 推送消息
     * @param topic
     * @param args
     */
    public void publish(String topic, Object... args) {
        doPublish(topic, args);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Future<?> publishWaitAsync(String topic, Object... args) {
        List<Future<?>> lst = doPublish(topic, args);
        return new CollecFuture(lst);
    }

    protected boolean doSubscribe(String topic, Handler handler) {
        Map<Object, Handler> map = handlers.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        map.put(handler.getMethod(), handler);
        return true;
        // List<Handler> lst = handlers.computeIfAbsent(topic, t -> new CopyOnWriteArrayList<>())
        // return lst.add(handler)
    }

    protected List<Future<?>> doPublish(String topic, Object... args) {
        Map<Object, Handler> hdlmap = handlers.get(topic);
        if (hdlmap == null || hdlmap.isEmpty()) {
            return Collections.emptyList();
        }
        List<Future<?>> fls = new ArrayList<>(4); // 数据备份， 防止执行过程中更改
        List<Handler> rms = new ArrayList<>(1);
        hdlmap.values().stream().sorted().forEach(hdl -> {
            if (hdl.isOnce()) {
                rms.add(hdl);
            }
            Actuator actuator = hdl.exec(args);
            if (actuator == null) {
                return; // 参数不匹配， 无法处理
            }
            if (hdl.isSync()) {
                actuator.run();
            } else {
                String thread = hdl.getThread();
                if (hdl.getAnnotation().requst()) {
                    fls.add(submitAsync1(hdl, thread, actuator)); // 异步处理
                } else {
                    fls.add(submitAsync2(hdl, thread, actuator));
                }
            }
        });
        if (!rms.isEmpty()) {
            rms.forEach(hdl -> hdlmap.remove(hdl.getMethod())); // 一次执行后删除
            if (hdlmap.isEmpty()) {
                handlers.remove(topic);
            }
        }

        return fls;
    }

    //====================================================================================================
    //====================================================================================================
    //====================================================================================================
    // 多线程， 框架引用后可对该内容进行修改， 完成符合当前平台多线程处理方案

    protected Future<?> submitAsync1(Handler hdl, String thread, Runnable actuator) {
        // 对接Kratos框架的内容， 移动到fwk中的service中完成
        // List<String> caches = new ArrayList<>(Arrays.asList(hdl.getAnnotation().caches()));
        // boolean threadCache = caches.remove(Kratos.PRE_THREAD + "*");
        // ThreadTransfer tt = new ThreadTransfer(true, threadCache, true);
        // // 绑定额外的信息绑定信息
        // Arrays.asList(hdl.getAnnotation().caches()).forEach(k -> tt.addCacheValue(k, KratosX.getProperty(k, null)));
        // // 构建新的执行单元
        // Runnable runable0 = () -> { // 重做执行器
        //   try {
        //     ThreadTransfer.reduce(tt);
        //     actuator.run();
        //   } finally {
        //     ThreadTransfer.destory(tt);
        //   }
        // };
        // return KratosX.getServiceExecutor(thread).submit(runable0);
        if (hdl.getAnnotation().delay() > 0) {
            return ThreadService.getService().schedule(actuator, hdl.getAnnotation().delay(), TimeUnit.MILLISECONDS);
        }
        return ThreadService.getService().submit(actuator); // 系统中的备用解决方法，实则无法处理多线程奋力和凭据传递内容
      }

    protected Future<?> submitAsync2(Handler hdl, String thread, Runnable actuator) {
        // 对接Kratos框架的内容， 移动到fwk中的service中完成
        // return KratosX.getServiceExecutor(thread).submit(actuator);
        if (hdl.getAnnotation().delay() > 0) {
            return ThreadService.getService().schedule(actuator, hdl.getAnnotation().delay(), TimeUnit.MILLISECONDS);
        }
        return ThreadService.getService().submit(actuator); // 系统中的备用解决方法，实则无法处理多线程奋力和凭据传递内容
    }
}
