package com.example.commons.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConvertUtils {
    public static <T> Map<Serializable,T> toMap(Collection<T> c, Function<T, Serializable> func){
        if(CollUtil.isEmpty(c)){
            return Collections.emptyMap();
        }
        Map<Serializable,T> rtnMap = new HashMap<>();
        for (T t : c) {
            Serializable apply = func.apply(t);
            rtnMap.put(String.valueOf(apply),t);
        }
        return rtnMap;
    }

    public static Set<Serializable> toSet(Collection<? extends Serializable> list){
        if(CollUtil.isEmpty(list)){
            return Collections.emptySet();
        }
        Set<Serializable> rtnSet = new HashSet<>();
        for (Serializable serializable : list) {
            if(serializable != null){
                rtnSet.add(String.valueOf(serializable));
            }
        }
        return rtnSet;
    }

    public static <T> Set<Serializable> toSet(Collection<T> c, Function<T, Serializable> func){
        if(CollUtil.isEmpty(c)){
            return Collections.emptySet();
        }
        Set< Serializable> rtnSet = new HashSet<>();
        for (T t : c) {
            Serializable apply = func.apply(t);
            if(apply == null){
                continue;
            }
            rtnSet.add(apply);
        }
        return rtnSet;
    }

    //  匹配 将C设置给P  通过一个key进行维系 这里是一对一的关系
    public static<P,C> void match(Collection<P> pList, Collection<C> cList, Function<P,Serializable> pKey,
                                  Function<C,Serializable> cKey, BiConsumer<P,C> callback){
        if(CollUtil.isEmpty(pList) || CollUtil.isEmpty(cList)){
            return ;
        }

        Map<Serializable, C> cMap = toMap(cList, cKey);
        for (P p : pList) {
            Serializable pKey0 = pKey.apply(p);
            C c = cMap.get(pKey0);
            if(c != null){
                callback.accept(p,c);
            }
        }
    }

    public static<T> List<T> group(Collection<T> list,Function<T,Serializable> func,BiFunction<T,T,Void> cb){
        Map<Serializable,T> rtn = new HashMap();

        for (T t : list) {
            Serializable key = func.apply(t);
            //  null不进行处理
            if(key == null){
                continue;
            }
            T v = rtn.get(key);
            if(v == null){
                rtn.put(key,t);
                continue;
            }

            //  回调作出处理
            cb.apply(v,t);
        }

        return new ArrayList<>(rtn.values());
    }

    public static <P,C> void collect(Collection<P> pList,Collection<C> cList,Function<P,C> func){
        if(CollUtil.isEmpty(pList) || CollUtil.isEmpty(cList)){
            return ;
        }
        for (P p : pList) {
            C c = func.apply(p);
            if(c != null){
                cList.add(c);
            }
        }
    }

    public static <P,C> void collectMulti(Collection<P> pList,Collection<C> cList,Function<P,? extends Collection<C>> func){
        if(CollUtil.isEmpty(pList) || CollUtil.isEmpty(cList)){
            return ;
        }
        for (P p : pList) {
            Collection cs = func.apply(p);
            if(cs != null){
                cList.addAll(cs);
            }
        }
    }

    public static <T> List<T> subList(List<T> origin, Pageable pageable){
        if(pageable == null){
            return origin;
        }

        int start = ((int) pageable.getOffset());
        int end = start + pageable.getPageSize();
        List<T> rtn = origin.subList(Math.min(start, origin.size()), Math.min(origin.size(), end));

        return rtn;
    }

    public static List<String> split(String str,String sep){
        if(StrUtil.isEmpty(str)){
            return new ArrayList<>();
        }

        String[] split = str.split(sep);
        List<String> rtnList = new ArrayList<>();

        for (String s : split) {
            if(StrUtil.isNotEmpty(s)){
                rtnList.add(s);
            }
        }

        return rtnList;
    }

    public static <P,C> List<C> fieldList(List<P> pList,Function<P,C> func){
        List<C> cList = new ArrayList<>();
        for (P p : pList) {
            C apply = func.apply(p);
            cList.add(apply);
        }

        return cList;
    }

    public static <T> String join(List<? extends T> list,Function<T,? extends Serializable> func){
        List<String> collect = list.stream()
                .map(item -> {
                    return String.valueOf(func.apply(item));
                })
                .collect(Collectors.toList());

        return join(collect);

    }

    public static String join(List<String> origin){
        if(origin == null || origin.isEmpty()){
            return "";
        }
        StringJoiner sj = new StringJoiner(",");
        for (String s : origin) {
            sj.add(s);
        }
        return sj.toString();
    }

    public static String join(String[] origin){
        if(origin == null || origin.length == 0){
            return "";
        }
        StringJoiner sj = new StringJoiner(",");
        for (String s : origin) {
            sj.add(s);
        }
        return sj.toString();
    }

    public static String paramConvert (String origin){
        return origin.replace("&","\\u0026")
                .replace("<","\\u003c")
                .replace(">","\\u003e");
    }

    public static<T> void combine(List<T> pre, List<T> next,Function<T,Serializable> key){
        Set<Serializable> preSet = toSet(pre, key);
        int index = 0;
        for (T t : next) {
            Serializable apply = key.apply(t);
            if(!preSet.contains(apply)){
                pre.add(t);
            }
            index++;
        }
    }

    //  kv同步处理
    public static<T,V> void combine(List<T> pre, List<T> next,List<V> pre1, List<V> next1,Function<T,Serializable> key){
        Set<Serializable> preSet = toSet(pre, key);
        int index = 0;
        for (T t : next) {
            Serializable apply = key.apply(t);
            if(!preSet.contains(apply)){
                pre.add(t);
                //  这里同步添加
                pre1.add(next1.get(index));
            }
            index++;
        }
    }

    public static<T> List<T> exclude(List<T> origin,List<T> excludeList,Function<T,Serializable> func){
        List<T> rtnList = new ArrayList<>();
        Set<Serializable> excludeSet = toSet(excludeList, func);

        for (T t : origin) {
            Serializable apply = func.apply(t);
            if(excludeSet.contains(apply)){
                continue;
            }
            rtnList.add(t);
        }

        return rtnList;
    }

    //  复制一份数据
    public static <T> T copy(T origin){
        if(origin == null){
            return null;
        }

        T rtn = null;
        try{
            rtn = (T) origin.getClass().getConstructor().newInstance();
        }catch (Exception e){
            throw new RuntimeException();
        }

        BeanUtils.copyProperties(origin,rtn);

        return rtn;
    }

    public static <T>  Map<String,Object> toMap(T t){
        Map rtn = new HashMap();
        BeanUtils.copyProperties(t,rtn);
        return rtn;
    }

    public static <T> T toJava(Map<String,Object> map,Class<? extends T> clz){
        T t = ReflectUtil.newInstance(clz);
        BeanUtils.copyProperties(map,t);
        return t;
    }

    //  utf-8 转换城 iso  微信需要这个
    public static String iso(String origin) {
        return new String(origin.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1);
    }

    public static <T> List<T>  distinct(List<T> origin,Function<T,Serializable> key){
        if(CollUtil.isEmpty(origin)){
            return Collections.emptyList();
        }
        List<T> dest = new ArrayList<>();
        Set<Serializable> set = new HashSet<>();
        for (T t : origin) {
            Serializable apply = key.apply(t);
            if(set.contains(apply)){
                continue;
            }
            set.add(apply);
            dest.add(t);
        }
        return dest;
    }

    //通过es返回的顺序   有序的构建mongo Documents
    public static <T,E> List<E> commodityConvert(List<T> commodity, List<E> commodityDBS, Function<T,Serializable> tKey,Function<E,Serializable> eKey,BiFunction<T,E,Void> func){
        Map<Serializable, E> map = ConvertUtils.toMap(commodityDBS, eKey);
        List<E> rtn = new LinkedList<>();

        for (T t : commodity) {
            Serializable apply = tKey.apply(t);
            E e = map.get(apply);
            if(e == null){
                continue;
            }
            if(func != null){
                func.apply(t,e);
            }
            rtn.add(e);

        }

        return rtn;
    }
}
