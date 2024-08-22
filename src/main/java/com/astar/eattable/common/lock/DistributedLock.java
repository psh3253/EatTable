package com.astar.eattable.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key();
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    long leaseTime() default 2L;
    long waitTime() default 5L;
}
