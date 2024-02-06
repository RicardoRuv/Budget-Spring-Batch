package com.example.budgetspringbatch.repo;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class DatabaseVerificationConfig {

    private static final Logger logger =  LoggerFactory.getLogger(DatabaseVerificationConfig.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void verifyDatabase(){
        verifyH2Database();
        verifyMongoDatabase();
    }

    private void verifyH2Database(){
        try{
            jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
            logger.info("H2 Database is up and running");
        }catch (Exception e){
            logger.info("Error while verifying H2 Database");
        }
    }
    private  void verifyMongoDatabase(){
        try{
           String name=  mongoTemplate.getDb().getName();
            logger.info("Mongo Database is up and running "+ name);
        }catch (Exception e){
            logger.info("Error while verifying Mongo Database");
        }
    }
}
