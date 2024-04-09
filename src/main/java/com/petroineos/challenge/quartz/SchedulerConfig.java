package com.petroineos.challenge.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

/**
 * the configuration for the Quartz scheduling system is defined.
 * It includes the basic configuration parameters for the Quartz framework.
 * The specific parameter values are stored in the "resources/config/quartz.properties" file.
 * @Author Dean Zhu
 * @Date 2024-04-08
 * @Version 1.0
 */
@Configuration
public class SchedulerConfig {
    private final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    private static final String PETROINEOS_QUARTZ_CONFIG_PATH = "config/quartz.properties";

    @Bean(name="stdSchedulerFactory")
    public StdSchedulerFactory stdSchedulerFactory() throws SchedulerException {
        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(quartzProperties());
        return stdSchedulerFactory;
    }

    @Bean
    @ConditionalOnResource(resources = PETROINEOS_QUARTZ_CONFIG_PATH)
    Properties quartzProperties() {
        try {
            PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
            propertiesFactoryBean.setLocation(new ClassPathResource(PETROINEOS_QUARTZ_CONFIG_PATH));
            propertiesFactoryBean.afterPropertiesSet();
            return propertiesFactoryBean.getObject();
        } catch (IOException e) {
            logger.error("Setup quartz properties failed, config file location: {}", PETROINEOS_QUARTZ_CONFIG_PATH,  e);
        }
        return null;
    }

    @Bean
    public QuartzInitializerListener quartzInitializerListener() {
        return new QuartzInitializerListener();
    }

    @Bean
    public Scheduler powerTradeAggregationScheduler() throws SchedulerException {
        return stdSchedulerFactory().getScheduler();
    }
}
