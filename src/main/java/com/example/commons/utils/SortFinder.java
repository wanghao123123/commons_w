package com.example.commons.utils;

import cn.hutool.core.collection.CollUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @auther hao.wang
 * @date 2021/5/12
 * 在排序中取出合适的数据  根据区间取值一个左闭区间，比如
 * [1,4,7,8,12] 如果是3取值1 如果是8取值7 如果是13 取值12
 * >    通常用于时间段的取值
 */
public class SortFinder<T> {
    private List<T> origin;
    Function<T,? extends Number> sort;

    public SortFinder(List<T> origin, Function<T,? extends Number> sort) {
        this.origin = origin;
        this.sort = sort;
        if(CollUtil.isEmpty(origin)){
            return ;
        }
        Collections.sort(origin,(a,b) -> {
            Number a1 = sort.apply(a);
            Number b1 = sort.apply(b);
            return (int)(a1.longValue() - b1.longValue());
        });
    }

    //  查找合适实例 左闭方式  直接遍历的方式进行查找
    public T querySuit(Number key){
        T pre = null;
        for (T t : origin) {
            if(pre == null){
                pre = t;
                continue;
            }
            long a1 = sort.apply(t).longValue();

            //  如果超过了预定的值 那么取值前面一个
            if(a1 >= key.longValue()){
                break;
            }

            pre = t;
        }
        return pre;
    }
}
