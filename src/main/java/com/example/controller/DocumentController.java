package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DocumentController {

    @Value("${document.collectionName}")
    private String defaultCollectionName;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DocumentController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/save")
    public void saveDocument(@RequestBody Document document) {
        mongoTemplate.save(document, defaultCollectionName);
    }

    @GetMapping("/retrieve/{collectionName}")
    public List<Object> retrieveDocuments(@PathVariable String collectionName) {
        return mongoTemplate.findAll(Object.class, collectionName);
    }

    @GetMapping("/retrieve/{collectionName}/{fieldName}/{fieldValue}")
    public List<Object> retrieveDocumentsByField(
            @PathVariable String collectionName,
            @PathVariable String fieldName,
            @PathVariable String fieldValue) {

        Query query = new Query(Criteria.where(fieldName).is(fieldValue));
        return mongoTemplate.find(query, Object.class, defaultCollectionName);
    }

    @GetMapping("/filterIdField")
    public List<Object> retrieveDocumentsWithoutId() {
        Query query = new Query();
        ProjectionOperation projection = Aggregation.project().andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(projection);
        return mongoTemplate.aggregate(aggregation, defaultCollectionName, Object.class).getMappedResults();
    }


    // Calculate average price for each category
    @GetMapping("/avg-price-by-category")
    public List<Document> calculateAveragePriceByCategory() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("category").avg("price").as("avgPrice")
        );
        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, defaultCollectionName, Document.class);
        return result.getMappedResults();
    }

    // Find products with price greater than 50 and sort by name
    @GetMapping("/expensive-products")
    public List<Document> findExpensiveProducts() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("price").gt(50)),
                Aggregation.sort(Sort.Direction.ASC, "name")
        );
        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, defaultCollectionName, Document.class);
        return result.getMappedResults();
    }

    // Join products with reviews
    @GetMapping("/products-with-reviews")
    public List<Document> findProductsWithReviews() {
        LookupOperation lookup = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("productId")
                .as("reviews");

        Aggregation aggregation = Aggregation.newAggregation(lookup);
        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, defaultCollectionName, Document.class);
//        result.forEach();
        return result.getMappedResults();
    }

    // Group documents by month and calculate total sales
    @GetMapping("/monthly-sales")
    public List<Document> calculateMonthlySales() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .andExpression("year(date)").as("year")
                        .andExpression("month(date)").as("month")
                        .and("sales").as("sales"),
                Aggregation.group(Fields.from(Fields.field("year"), Fields.field("month")))
                        .sum("sales").as("totalSales")
        );
        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, defaultCollectionName, Document.class);
        return result.getMappedResults();
    }
}

