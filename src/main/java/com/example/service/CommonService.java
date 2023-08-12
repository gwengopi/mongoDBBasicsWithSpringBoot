package com.example.service;

import com.example.entity.CommonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonService {
    @Autowired
    MongoTemplate mongoTemplate;

    public void add(CommonObject commonObject) {
        mongoTemplate.save(commonObject);
    }

    public void addObject(Object object) {
        mongoTemplate.save(object, "inventory");
    }

    public List<Object> findProductsByRatingGreaterThan(Object minRating) {
        Criteria criteria = Criteria.where("reviews.rating").gt(minRating);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Object.class);
    }
}
