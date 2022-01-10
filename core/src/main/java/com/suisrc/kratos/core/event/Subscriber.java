package com.suisrc.kratos.core.event;

import com.suisrc.kratos.core.annotation.Subscribe.SubscribeType;

/**
 * 继承该接口的类会被自动订阅
 * 
 * 注意：继承该接口的实现，如果需要加载监听，需要使用@Subscribe注解标记
 * 
 * @author Y13
 *
 */
public interface Subscriber {

  /**
   * 控制事件总线的名称
   * @return
   */
  default String getTopic() {
    return null;
  }

  /**
   * 默认线程池
   * @return
   */
  default String getThread() {
    return null;
  }

  /**
   * 是否使用同步事件总线，该内容谨慎或不推荐使用
   * 
   * 除非特殊情况，否者严禁使用同步事件总线
   * @return
   */
  default SubscribeType getSubscribeType() {
    return SubscribeType.NONE;
  }

}
