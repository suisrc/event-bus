package com.suisrc.kratos.eventbus.mananger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.suisrc.kratos.core.annotation.Subscribe;
import com.suisrc.kratos.core.annotation.Subscribe.SubscribeType;
import com.suisrc.kratos.core.event.EventBus;
import com.suisrc.kratos.core.event.SubscribeHandler;
import com.suisrc.kratos.core.event.Subscriber;
import com.suisrc.kratos.eventbus.service.EventBusService;

/**
 * 事件总线管理器, 默认实现
 * 
 * @author Y13
 *
 */
// @ApplicationScoped
// @Log
public abstract class AbstractEventBusManager implements EventBusManager {
  private static final Logger logger = Logger.getLogger(AbstractEventBusManager.class.getName());

  private final EventBusService delegate = new EventBusService();

  /**
   * 是否加载
   */
  private boolean isLoaded;

  /**
   * @see Subscriber
   * @see SubscribeHandler
   * @return
   */
  public abstract List<Object> getSubscribers();

  /**
   * 
   */
  @Override
  public void load() {
    if (isLoaded) {
      throw new IllegalArgumentException(getClass() + " 已经执行加载load，不可重置执行");
    }
    isLoaded = true;

    List<Object> subscribes = getSubscribers();
    if (subscribes == null || subscribes.isEmpty()) {
      logger.info("无消息订阅服务");
      return;
    }
    // 执行订阅
    int count = 0;
    for (Object subscribe : subscribes) {
      count += this.subscribe(subscribe);
    }
    // subscribes.forEach(this::subscribe)
    String msg = "统计实际加载消息订阅服务数量：" + count;
    logger.info(msg);
  }

  /**
   * 
   */
  @Override
  public EventBusService getEventBusService() {
    return delegate;
  }

  // ====================================================================================================
  // ====================================================================================================
  // ====================================================================================================

  protected boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * 
   */
  @Override
  public int subscribe(Object obj) {
    if (obj == null) {
      return 0;
    }
    int count = 0;

    String topic0 = "default";
    String thread0 = "event-bus";
    SubscribeType stype0 = SubscribeType.ASYNC;

    if (obj instanceof Subscriber) {

      Subscriber subscribe = (Subscriber) obj;

      if (!isEmpty(subscribe.getTopic())) {
        topic0 = subscribe.getTopic();
      }
      if (!isEmpty(subscribe.getThread())) {
        thread0 = subscribe.getThread();
      }
      if (subscribe.getSubscribeType() != SubscribeType.NONE) {
        stype0 = subscribe.getSubscribeType();
      }
    }

    for (Method method : obj.getClass().getMethods()) {
      boolean result = suscribe(obj, method, topic0, thread0, stype0);
      if (result) {
        count++;
      }
    }
    return count;
  }

  protected boolean suscribe(Object obj, Method method, String topic0, String thread0, SubscribeType stype0) {
    
    Subscribe subscribe = method.getAnnotation(Subscribe.class);
    if (subscribe == null) {
      return false;
    }

    String topic1 = !isEmpty(subscribe.topic()) ? subscribe.topic() : topic0;
    String thread1 = !isEmpty(subscribe.thread()) ? subscribe.thread() : thread0;
    SubscribeType stype1 = subscribe.type() != SubscribeType.NONE ? subscribe.type() : stype0;

    boolean result = false;
    switch (stype1) {
      case SYNC:
        result = delegate.subscribe(topic1, thread1, obj, method, subscribe);
        break;
      case ASYNC:
        result = delegate.subscribeAsync(topic1, thread1, obj, method, subscribe);
        break;
      case ONCE_SYNC:
        result = delegate.subscribeOnce(topic1, thread1, obj, method, subscribe);
        break;
      case ONCE_ASYNC:
        result = delegate.subscribeOnceAsync(topic1, thread1, obj, method, subscribe);
        break;
      default:
        break;
    }
    if (result) {
      logger.log(Level.INFO, "已经加载订阅器：主题[{0}], 方法[{2}::{3}], 线程池[{1}]", //
        new Object[]{topic1, thread1, method.getDeclaringClass().getName(),  method.getName()});
    }
    return result;
  }

  /**
   * 
   */
  @Override
  public int unsubscribe(Object obj) {
    if (obj == null) {
      return 0;
    }
    int count = 0;

    String topic0 = "default";
    if (obj instanceof Subscriber) {
      Subscriber subscribe = (Subscriber) obj;
      topic0 = isEmpty(subscribe.getTopic()) ? subscribe.getTopic() : topic0;
    }
    for (Method method : obj.getClass().getMethods()) {
      Subscribe subscribe = method.getAnnotation(Subscribe.class);
      if (subscribe != null) {

        String topic1 = isEmpty(subscribe.topic()) ? subscribe.topic() : topic0;
        if (delegate.unsubscribe(topic1, method)) {
          count++;
        }
      }
    }
    return count;
  }

}
