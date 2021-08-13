package com.example.commons.utils.mongo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hao.wang
 * @date Created in 2021/6/22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    String propName() default "";

    Type type() default Type.EQUAL;

    String joinName() default "";

    Join join() default Join.LEFT;

    String blurry() default "";

    enum Join {
        LEFT,
        RIGHT,
        INNER;

        Join() {
        }
    }

    enum Type {
        EQUAL,
        GREATER_THAN,
        LESS_THAN,
        INNER_LIKE,
        LEFT_LIKE,
        RIGHT_LIKE,
        LESS_THAN_NQ,
        IN,
        NOT_EQUAL,
        BETWEEN,
        NOT_NULL,
        IS_NULL,
        GT,
        GTE,
        LT,
        LTE,
        LIKE,
        START_WITH;

        Type() {
        }
    }
}
