package com.example.commons.utils.mongo;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @auther hao.wang
 * @date 2021/6/7
 */
public class QueryUtils {
    public static Query byId(Serializable id){
        return Query.query(Criteria.where("_id").is(id));
    }

    public static Query byIds(List<Serializable> ids){
        return Query.query(Criteria.where("_id").in(ids));
    }

    public static Query byIds(Collection<Serializable> ids){
        return Query.query(Criteria.where("_id").in(ids));
    }
}
