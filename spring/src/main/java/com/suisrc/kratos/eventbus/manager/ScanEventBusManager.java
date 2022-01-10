package com.suisrc.kratos.eventbus.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.suisrc.kratos.core.event.SubscribeHandler;
import com.suisrc.kratos.core.event.Subscriber;
import com.suisrc.kratos.eventbus.mananger.AbstractEventBusManager;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * 加载所有总线的订阅
 * 
 * @see ExternalSubscriber
 * @see ExternalSubscribeHandler
 */
@Configuration
public class ScanEventBusManager extends AbstractEventBusManager implements ApplicationContextAware {

    // private final Environment environment;
    // @Autowired
    // public ScanEventBusManager(Environment env) {
    //     environment = env;
    // }

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        load(); // 加载所有的订阅内容
    }

    @Override
    public List<Object> getSubscribers() {
        Set<Object> subscribers = new HashSet<>();
        Map<String, Subscriber> sub1 = context.getBeansOfType(Subscriber.class);
        subscribers.addAll(sub1.values());

        Map<String, SubscribeHandler> sub2 = context.getBeansOfType(SubscribeHandler.class);
        for ( SubscribeHandler sh : sub2.values()) {
            subscribers.addAll(sh.getSubscribers());
        }
        return new ArrayList<>(subscribers);
    }

    // @Override
    // public EventBusService getEventBusService() {
    //     return super.getEventBusService();
    // }
}
