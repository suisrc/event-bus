package com.suisrc.kratos.core.event;

import java.util.List;

/**
 * 继承该接口的类会被自动订阅
 * 
 * 注意：该接口是自动订阅加载类，和AutoSubscribe需要区别对待，通过getSubscribes获取加载实体
 * 
 * @author Y13
 *
 */
public interface SubscribeHandler {
  
  /**
   * 获取订阅的对象
   * @return
   */
  List<Object> getSubscribers();
}
