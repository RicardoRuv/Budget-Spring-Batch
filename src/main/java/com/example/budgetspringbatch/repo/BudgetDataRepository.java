package com.example.budgetspringbatch.repo;

import com.example.budgetspringbatch.model.BudgetData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BudgetDataRepository extends MongoRepository<BudgetData, String> {
}
