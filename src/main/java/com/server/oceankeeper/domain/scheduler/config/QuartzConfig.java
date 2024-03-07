package com.server.oceankeeper.domain.scheduler.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class QuartzConfig {
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final QuartzProperties quartzProperties;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
                                                     ApplicationContext applicationContext) {
        QuartzJobFactory quartzJobFactory = new QuartzJobFactory();
        quartzJobFactory.setApplicationContext(applicationContext);

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        schedulerFactoryBean.setQuartzProperties(properties);

        schedulerFactoryBean.setJobFactory(quartzJobFactory);
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setTransactionManager(transactionManager);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");

        return schedulerFactoryBean;
    }

//    @Bean
//    public Properties quartzProperties() {
//        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
//        yamlPropertiesFactoryBean.setResources(new ClassPathResource("quartz/quartz.yml"));
//        yamlPropertiesFactoryBean.afterPropertiesSet();
//        return yamlPropertiesFactoryBean.getObject();
//    }
}