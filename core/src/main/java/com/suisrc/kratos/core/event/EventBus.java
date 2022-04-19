package com.suisrc.kratos.core.event;

import java.util.concurrent.Future;

/**
 * 用于发布消息
 * 
 * @author Y13
 *
 */
public interface EventBus {

  /**
   * 
   * @param topic
   * @return
   */
	boolean hasTopic(String topic);
  
  /**
   * 向指定订阅总线发布消息
   * 
   * @param topic
   * @param event
   */
  void publish(String topic,  Object... event);

  /**
   * 向指定订阅总线发布消息，等待异步消息
   * @param topic
   * @param event
   */
  Future<?> publishWaitAsync(String topic, Object... event);

  //============================================================

  /**
   * 注册订阅对象，同一个主题， 同一个方法互斥
   * 
   * @param obj
   * @return
   */
  int subscribe(Object obj);

  /**
   * 非原始内容也能取消订阅, 主题和方法一致即可
   * 
   * @param subscribe
   * @return
   */
  int unsubscribe(Object obj);

  /**
   * 注销
   */
  default void destory() {
  }

  /**
   * 验证有效
   */
  default boolean invalid() {
    return true;
  }
}
