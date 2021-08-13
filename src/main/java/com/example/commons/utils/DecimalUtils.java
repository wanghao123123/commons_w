package com.example.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static cn.hutool.core.util.NumberUtil.toBigDecimal;

/**
 * @author hao.wang
 * @date Created in 2021/7/23
 */
public class DecimalUtils {

    /**
     * 加法计算（result = x + y）
     * @param x 被加数（可为null）
     * @param y 加数 （可为null）
     * @return 和 （可为null）
     * @author dengcs
     */
    public static BigDecimal add(BigDecimal x, BigDecimal y) {
        if (x == null) {
            return y;
        }
        if (y == null) {
            return x;
        }
        return x.add(y);
    }

    /**
     * 减法计算(result = x - y)
     * @param x 被减数（可为null）
     * @param y 减数（可为null）
     * @return BigDecimal 差 （可为null）
     * @author dengcs
     */
    public static BigDecimal subtract(BigDecimal x, BigDecimal y) {
        if (x == null || y == null) {
            return null;
        }
        return x.subtract(y);
    }

    /**
     * 乘法计算(result = x × y)
     * @param x 乘数(可为null)
     * @param y 乘数(可为null)
     * @return BigDecimal 积
     * @author dengcs
     */
    public static BigDecimal multiply(BigDecimal x, BigDecimal y) {
        if (x == null || y == null) {
            return null;
        }
        return x.multiply(y);
    }

    /**
     * 除法计算(result = x ÷ y)
     * @param x 被除数（可为null）
     * @param y 除数（可为null）
     * @return 商 （可为null,四舍五入，默认保留20位小数）
     * @author dengcs
     */
    public static BigDecimal divide(BigDecimal x, BigDecimal y) {
        if (x == null || y == null || y.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return stripTrailingZeros(x.divide(y, RoundingMode.HALF_UP));
    }

    /**
     * 倍数计算，用于单位换算
     * @param x        目标数(可为null)
     * @param multiple 倍数 (可为null)
     * @return BigDecimal (可为null)
     * @author dengcs
     */
    public static BigDecimal multiple(BigDecimal x, Integer multiple) {
        if (x == null || multiple == null) {
            return null;
        }
        return DecimalUtils.multiply(x, toBigDecimal(multiple));
    }

    /**
     * 去除小数点后的0（如: 输入1.000返回1）
     * @param x 目标数(可为null)
     * @return
     */
    public static BigDecimal stripTrailingZeros(BigDecimal x) {
        if (x == null) {
            return null;
        }
        return x.stripTrailingZeros();
    }

    /**
     * 等于
     * @param x
     * @param y
     * @return
     */
    public static boolean eq(BigDecimal x,BigDecimal y){
        return x.compareTo(y) == 0?true:false;
    }

    /**
     * 小于
     * @param x
     * @param y
     * @return
     */
    public static boolean lt(BigDecimal x,BigDecimal y){
        return x.compareTo(y) == -1?true:false;
    }

    /**
     * 大于
     * @param x
     * @param y
     * @return
     */
    public static boolean gt(BigDecimal x,BigDecimal y){
        return x.compareTo(y) == 1?true:false;
    }

    /**
     * 大于等于
     * @param x
     * @param y
     * @return
     */
    public static boolean gte(BigDecimal x,BigDecimal y){
        return x.compareTo(y) > -1?true:false;
    }

    /**
     * 小于等于
     * @param x
     * @param y
     * @return
     */
    public static boolean lte(BigDecimal x,BigDecimal y){
        return x.compareTo(y) <1?true:false;
    }
}
