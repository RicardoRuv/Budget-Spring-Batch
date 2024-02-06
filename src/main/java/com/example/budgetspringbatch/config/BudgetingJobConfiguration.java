package com.example.budgetspringbatch.config;

import com.example.budgetspringbatch.model.BudgetData;
import com.example.budgetspringbatch.repo.BudgetDataRepository;
import com.example.budgetspringbatch.processor.LoggingItemProcessor;
import com.example.budgetspringbatch.writer.MongoDBItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
public class BudgetingJobConfiguration {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private BudgetDataRepository budgetDataRepository;

    private DelimitedLineTokenizer createTokenizer(String[] names) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(names);
        return tokenizer;
    }

    private BeanWrapperFieldSetMapper<BudgetData> createFieldSetMapper() {
        BeanWrapperFieldSetMapper<BudgetData> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BudgetData.class);
        return fieldSetMapper;
    }

    private DefaultLineMapper<BudgetData> createLineMapper(String[] names) {
        DefaultLineMapper<BudgetData> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(createTokenizer(names));
        lineMapper.setFieldSetMapper(createFieldSetMapper());
        return lineMapper;
    }

    private FlatFileItemReader<BudgetData> createItemReader(String filePath, String[] names) {
        FlatFileItemReader<BudgetData> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filePath));
        reader.setLineMapper(createLineMapper(names));
        return reader;
    }

    @Bean
    public FlatFileItemReader<BudgetData> reader() {
        return createItemReader("src/main/resources/MOCK_DATA.csv", new String[]{"name", "lastName", "age", "jobTitle", "salary", "expenses"});
    }

    @Bean
    public FlatFileItemReader<BudgetData> csvReader() {
        return createItemReader("staging/processed_data.csv", new String[]{"name", "lastName", "age", "jobTitle", "salary", "expenses", "netSavings"});
    }

    @Bean
    public Step step1(){
        return new StepBuilder("step1", jobRepository)
                .<BudgetData, BudgetData>chunk(100, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Step step2(){
        return new StepBuilder("mongoWriteStep", jobRepository)
                .<BudgetData, BudgetData>chunk(100, transactionManager)
                .reader(csvReader())
                .writer(mongoWriter(budgetDataRepository))
                .build();
    }

    @Bean
    public Job readFlatFileJob(){
        return new JobBuilder("readFlatFileJob", jobRepository)
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public ItemProcessor<BudgetData, BudgetData> processor(){
        return new LoggingItemProcessor();
    }

    @Bean
    public ItemWriter<BudgetData> writer(){
        return csvWriter();
    }

    @Bean
    public FlatFileItemWriter<BudgetData> csvWriter() {
        BeanWrapperFieldExtractor<BudgetData> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"name", "lastName", "age", "jobTitle", "salary", "expenses", "netSavings"});

        DelimitedLineAggregator<BudgetData> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<BudgetData>()
                .name("budgetDataWriter")
                .resource(new FileSystemResource("staging/processed_data.csv"))
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    public ItemWriter<BudgetData> mongoWriter(BudgetDataRepository repository){
        return new MongoDBItemWriter(repository);
    }




}
