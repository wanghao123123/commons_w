package com.example.commons.utils.mongo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author hao.wang
 * @date Created in 2021/6/22
 */
public class QueryHelp {
    private static final Logger log = LoggerFactory.getLogger(QueryHelp.class);

    public QueryHelp() {
    }

    public static <Q> Query getQuery(Q q) {
        Query query = new Query();
        ReflectionUtils.doWithFields(q.getClass(), (f) -> {
            com.example.commons.utils.mongo.Query qi = f.getAnnotation(com.example.commons.utils.mongo.Query.class);
            f.setAccessible(true);
            String fieldName = StrUtil.isNotBlank(qi.propName()) ? qi.propName() : f.getName();
            Object value = f.get(q);
            if (value != null) {
                if (!StrUtil.isNotBlank(qi.blurry())) {
                    switch(qi.type()) {
                        case EQUAL:
                            query.addCriteria(Criteria.where(fieldName).is(value));
                            break;
                        case GT:
                            query.addCriteria(Criteria.where(fieldName).gt(value));
                            break;
                        case GTE:
                            query.addCriteria(Criteria.where(fieldName).gte(value));
                            break;
                        case LT:
                            query.addCriteria(Criteria.where(fieldName).lt(value));
                            break;
                        case LTE:
                            query.addCriteria(Criteria.where(fieldName).lte(value));
                            break;
                        case LIKE:
                            query.addCriteria(Criteria.where(fieldName).regex(".*" + value + ".*"));
                            break;
                        case BETWEEN:
                            List v = (List)value;
                            query.addCriteria(Criteria.where(fieldName).gte(v.get(0)).lt(v.get(1)));
                            break;
                        case START_WITH:
                            query.addCriteria(Criteria.where(fieldName).regex(value + ".*"));
                            break;
                        case IN:
                            Collection coll = (Collection)value;
                            if (CollUtil.isNotEmpty(coll)) {
                                query.addCriteria(Criteria.where(fieldName).in(coll));
                            }
                            break;
                        case NOT_EQUAL:
                            query.addCriteria(Criteria.where(fieldName).ne(value));
                            break;
                        case NOT_NULL:
                            query.addCriteria(Criteria.where(fieldName).exists(true));
                            break;
                        case IS_NULL:
                            query.addCriteria(Criteria.where(fieldName).is((Object)null));
                    }

                } else {
                    String[] split = qi.blurry().split(",");
                    Criteria[] criteriaList = new Criteria[split.length];

                    for(int i = 0; i < split.length; ++i) {
                        criteriaList[i] = Criteria.where(split[i]).regex(".*" + value + ".*");
                    }

                    query.addCriteria((new Criteria()).orOperator(criteriaList));
                }
            }
        }, (f) -> f.isAnnotationPresent(com.zzyy.leaf.tools.annotation.Query.class));
        return query;
    }

    public static <Q> Criteria getCriteria(Q q) {
        Criteria criteria = new Criteria();
        ReflectionUtils.doWithFields(q.getClass(), (f) -> {
            com.example.commons.utils.mongo.Query qi = f.getAnnotation(com.example.commons.utils.mongo.Query.class);
            f.setAccessible(true);
            String fieldName = StrUtil.isNotBlank(qi.propName()) ? qi.propName() : f.getName();
            Object value = f.get(q);
            if (value != null) {
                if (!StrUtil.isNotBlank(qi.blurry())) {
                    switch(qi.type()) {
                        case EQUAL:
                            criteria.and(fieldName).is(value);
                            break;
                        case GT:
                            criteria.and(fieldName).gt(value);
                            break;
                        case GTE:
                            criteria.and(fieldName).gte(value);
                            break;
                        case LT:
                            criteria.and(fieldName).lt(value);
                            break;
                        case LTE:
                            criteria.and(fieldName).lte(value);
                            break;
                        case LIKE:
                            criteria.and(fieldName).regex(".*" + value + ".*");
                            break;
                        case BETWEEN:
                            List v = (List)value;
                            criteria.and(fieldName).gte(v.get(0)).lt(v.get(1));
                            break;
                        case START_WITH:
                            criteria.and(fieldName).regex(value + ".*");
                            break;
                        case IN:
                            Collection coll = (Collection)value;
                            if (CollUtil.isNotEmpty(coll)) {
                                criteria.and(fieldName).in(coll);
                            }
                            break;
                        case NOT_EQUAL:
                            criteria.and(fieldName).ne(value);
                            break;
                        case NOT_NULL:
                            criteria.and(fieldName).exists(true);
                            break;
                        case IS_NULL:
                            criteria.and(fieldName).is((Object)null);
                    }

                } else {
                    String[] split = qi.blurry().split(",");
                    Criteria[] criteriaList = new Criteria[split.length];

                    for(int i = 0; i < split.length; ++i) {
                        criteriaList[i] = Criteria.where(split[i]).regex(".*" + value + ".*");
                    }

                    criteria.orOperator(criteriaList);
                }
            }
        }, (f) -> f.isAnnotationPresent(com.example.commons.utils.mongo.Query.class));
        return criteria;
    }

    private static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }

        return fields;
    }
}
