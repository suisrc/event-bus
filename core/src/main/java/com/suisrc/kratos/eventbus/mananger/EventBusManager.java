package com.suisrc.kratos.eventbus.mananger;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import com.suisrc.kratos.core.event.EventBus;
import com.suisrc.kratos.eventbus.service.CollecFuture;
import com.suisrc.kratos.eventbus.service.EventBusService;

/**
 * 事件总线管理器
 * 
 * @author Y13
 *
 */
public interface EventBusManager extends EventBus {

    /**
     * 加载订阅
     */
    void load();

  /**
   * <p>
   * 获取事件总线, 不构建新的，如果没有，返回值为null
   * 
   * @return
   */
  EventBusService getEventBusService();

  /**
   * 获取所有的主题
   * 
   * @return
   */
  default Set<String> getTopicAll() {
    return Optional.of(getEventBusService()).map(EventBusService::getTopicAll).orElse(Collections.emptySet());
  }

  // =====================================================================================
  // com.suisrc.kratos.core.event.EventBus

  /**
   * 
   * @param topic
   * @return
   */
  default boolean hasTopic(String topic) {
    return Optional.of(getEventBusService()).map(bus -> bus.hasSubscribe(topic)).orElse(false);
  }

  /**
   * 触发事件总线
   * 
   * @param topic
   * @param args
   */
  default void publish(String topic, Object... args) {
    Optional.of(getEventBusService()).ifPresent(bus -> bus.publish(topic, args));
  }

  /**
   * 触发事件总线
   * 
   * @param topic
   * @param args
   * @return
   */
  default Future<?> publishWaitAsync(String topic, Object... args) {
    return Optional.of(getEventBusService()).map(bus -> bus.publishWaitAsync(topic, args)).orElse(CollecFuture.empty());
  }

}
