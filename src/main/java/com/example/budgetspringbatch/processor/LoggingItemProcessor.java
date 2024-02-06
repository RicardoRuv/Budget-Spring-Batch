package com.example.budgetspringbatch.processor;

import com.example.budgetspringbatch.model.BudgetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class LoggingItemProcessor implements ItemProcessor<BudgetData, BudgetData> {

    private static final Logger log = LoggerFactory.getLogger(LoggingItemProcessor.class);

    @Override
    public BudgetData process(BudgetData item) throws Exception {
        BigDecimal salary = BigDecimal.valueOf(item.getSalary());
        BigDecimal expenses = BigDecimal.valueOf(item.getExpenses());
        BigDecimal netSavings = salary.subtract(expenses).setScale(2, RoundingMode.HALF_EVEN);
        item.setNetSavings(netSavings.doubleValue());
        log.info("Processing item: {}", item);
        return item;
    }
}
