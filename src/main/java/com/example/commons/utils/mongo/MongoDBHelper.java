package com.example.commons.utils.mongo;

import com.zzyy.leaf.enums.SortEnum;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author hWang
 */
@Component
public class MongoDBHelper {

    public static MongoDBHelper mongoDBHelper;
    private static final boolean del = false;

    @PostConstruct
    public void init() {
        mongoDBHelper = this;
        mongoDBHelper.mongoTemplate = this.mongoTemplate;
    }

    private static final int FIRST_PAGE_NUM = 1;

    private static final String ID = "_id";

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 逻辑删除
     * @param _id mongo id
     * @param z   类
     * @param <T>
     */
    public static <T> void remove(Serializable _id , Class<T> z) {
        remove(Arrays.asList(_id),z);
    }

    public static <T> void remove(Query query , Class<T> z) {
        Update update = new Update();
        update.set("del", true);
        update.set("delTime",System.currentTimeMillis());
        mongoDBHelper.mongoTemplate.update(z)
                .matching(query)
                .apply(update)
                .all();
    }

    /**
     * 批量逻辑删除
     * @param _ids
     * @param z
     * @param <T>
     */
    public static <T> void remove(List<Serializable> _ids, Class<T> z) {
        Update update = new Update();
        update.set("del", true);
        update.set("delTime",System.currentTimeMillis());
        mongoDBHelper.mongoTemplate.update(z)
                .matching(QueryUtils.byIds(_ids))
                .apply(update)
                .first();
    }


    public static <T> List<T> find(Query query,Class<T> z){
        Document queryObject = query.getQueryObject();
        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }
        return mongoDBHelper.mongoTemplate.find(query, z);
    }

    public static <T> T findOne(Query query,Class<T> z){
        Document queryObject = query.getQueryObject();
        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }
        return mongoDBHelper.mongoTemplate.findOne(query, z);
    }

    public static <T> T findById(Serializable id,Class<T> z){
        return findOne(new Query(Criteria.where(ID).is(id)),z);
    }

    public static <T> long count(Query query,Class<T> z){
        Document queryObject = query.getQueryObject();
        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }
        return mongoDBHelper.mongoTemplate.count(query,z);
    }

    /**
     * 功能描述: 分页查询列表信息
     *
     * @param clazz       对象类型
     * @param currentPage 当前页码
     * @param pageSize    分页大小
     * @return:java.util.List<T>
     */
    public static <T> PageImpl<T> selectListPage(Query query, String lastId, Class<T> clazz, Integer currentPage, Integer pageSize) {
        Document queryObject = query.getQueryObject();
        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }
        return selectListPage(query, lastId, clazz, currentPage, pageSize, SortEnum.DESC.getCode(), "createTime");
    }

    /**
     *
     * @param query       查询条件
     * @param lastId
     * @param clazz       对象类型
     * @param currentPage 当前页码
     * @param pageSize    分页大小
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T> PageImpl<T> selectListPage(Query query, String lastId, Class<T> clazz, Integer currentPage, Integer pageSize, Consumer<Query> consumer) {
        Document queryObject = query.getQueryObject();
        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }
        return selectListPage(query, lastId, clazz, currentPage, pageSize, SortEnum.DESC.getCode(), "createTime",consumer);
    }

    /**
     * @param query
     * @param lastId
     * @param clazz
     * @param currentPage
     * @param pageSize
     * @param sort        {@link com.zzyy.leaf.enums.SortEnum}
     * @param sortFileId
     * @param <T>
     * @return
     */
    public static <T> PageImpl<T> selectListPage(Query query, String lastId, Class<T> clazz, Integer currentPage, Integer pageSize, Integer sort, String sortFileId) {
        Document queryObject = query.getQueryObject();

        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }

        if (ObjectUtils.isEmpty(query)) {
            query = new Query();
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        //分页逻辑
        long total = mongoDBHelper.mongoTemplate.count(query, clazz);
        final Integer pages = (int) Math.ceil(total / (double) pageSize);
        if (currentPage == null || currentPage <= 0) {
            currentPage = FIRST_PAGE_NUM;
        }
        if (!StringUtils.isEmpty(lastId)) {
            Criteria criteria = new Criteria();
            if (currentPage != FIRST_PAGE_NUM) {
                criteria.and(ID).gt(new ObjectId(lastId));
            }
            query.addCriteria(criteria);
            query.limit(pageSize);
        } else {
            int skip = pageSize * (currentPage - 1);
            query.skip(skip).limit(pageSize);
        }
        query.with(Sort.by(sort==SortEnum.ASC.getCode() ? Sort.Direction.ASC : Sort.Direction.DESC, sortFileId));
        List<T> entityList = mongoDBHelper.mongoTemplate.find(query, clazz);
        return new PageImpl(entityList, PageRequest.of(currentPage - 1, pageSize), total);
    }

    public static <T> PageImpl<T> selectListPage(Query query, String lastId, Class<T> clazz,
                                                 Integer currentPage, Integer pageSize, Integer sort,
                                                 String sortFileId, Consumer<Query> consumer) {
        Document queryObject = query.getQueryObject();

        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }

        if (ObjectUtils.isEmpty(query)) {
            query = new Query();
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        //分页逻辑
        long total = mongoDBHelper.mongoTemplate.count(query, clazz);
        final Integer pages = (int) Math.ceil(total / (double) pageSize);
        if (currentPage == null || currentPage <= 0) {
            currentPage = FIRST_PAGE_NUM;
        }

        if (!StringUtils.isEmpty(lastId)) {
            Criteria criteria = new Criteria();
            if (currentPage != FIRST_PAGE_NUM) {
                criteria.and(ID).gt(new ObjectId(lastId));
            }
            query.addCriteria(criteria);
            query.limit(pageSize);
        } else {
            int skip = pageSize * (currentPage - 1);
            query.skip(skip).limit(pageSize);
        }
        consumer.accept(query);
        query.with(Sort.by(sort==SortEnum.ASC.getCode() ? Sort.Direction.ASC : Sort.Direction.DESC, sortFileId));
        List<T> entityList = mongoDBHelper.mongoTemplate.find(query, clazz);

        return new PageImpl(entityList, PageRequest.of(currentPage - 1, pageSize), total);
    }

    public static <T> PageImpl<T> selectListPage(Query query, Class<T> clazz, Integer currentPage, Integer pageSize) {
        Document queryObject = query.getQueryObject();

        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }

        if (ObjectUtils.isEmpty(query)) {
            query = new Query();
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        //分页逻辑
        long total = mongoDBHelper.mongoTemplate.count(query, clazz);
        final Integer pages = (int) Math.ceil(total / (double) pageSize);
        if (currentPage == null || currentPage <= 0) {
            currentPage = FIRST_PAGE_NUM;
        }
        int skip = pageSize * (currentPage - 1);
        query.skip(skip).limit(pageSize);
        List<T> entityList = mongoDBHelper.mongoTemplate.find(query, clazz);
        return new PageImpl(entityList, PageRequest.of(currentPage - 1, pageSize), total);
    }


    /**
     * 功能描述: 根据条件查询集合
     *
     * @param query 查询条件
     * @param clazz 对象类型
     * @return
     */
    public static <T> List<T> selectByQuery(Query query, Class<T> clazz) {
        Document queryObject = query.getQueryObject();
        if(queryObject.get("del")==null){
            query.addCriteria(Criteria.where("del").is(del));
        }
        return mongoDBHelper.mongoTemplate.find(query, clazz);
    }
}
