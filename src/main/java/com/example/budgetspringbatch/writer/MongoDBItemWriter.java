package com.example.budgetspringbatch.writer;

import com.example.budgetspringbatch.model.BudgetData;
import com.example.budgetspringbatch.repo.BudgetDataRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class MongoDBItemWriter implements ItemWriter<BudgetData> {

    private final BudgetDataRepository repository;

    public MongoDBItemWriter(BudgetDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends BudgetData> chunk) throws Exception {
        repository.saveAll(chunk);
    }
}
