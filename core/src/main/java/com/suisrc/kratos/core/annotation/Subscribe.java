
package com.suisrc.kratos.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> 订阅
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    /**
     * 订阅主题
     * @return
     */
    String topic() default "";

    /**
     * 订阅线程池
     * @return
     */
    String thread() default "";

    /**
     * 订阅类型
     * @return
     */
    SubscribeType type() default SubscribeType.NONE;

    /**
     * 事务， 异步时候有效
     * @return
     */
    boolean transactional() default false;

    /**
     * 用户信息, true: 使用KratoThread调用，通过ThreadTransfer转换当前线程上数据
     * @return 
     */
    boolean requst() default false;

    /**
     * 用户上下文内容，equst = true有效， 多线程处理， 传递用户身份, UserCtx.class
     * 如果框架登录类型唯一， 可以为空
     * @return
     */
    Class<?> login() default Void.class;

    /**
     * requst = true有效，转移更多的缓存信息， 比如：header.X-Request-Id, cookie.kat等
     * @return
     */
    String[] caches() default {}; // 缓存

    /**
     * 优先级
     * @return 
     */
    int order() default 1024;

    /**
     * 订阅类型
     */
    enum SubscribeType {
        NONE, SYNC, ASYNC, ONCE_SYNC, ONCE_ASYNC;
    }
}
