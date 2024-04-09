package com.petroineos.challenge.quartz;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * After the service is started, it will utilize the previously defined scheduler to schedule specific "jobs" using "triggers".
 * @Author Dean Zhu
 * @Date 2024-04-09
 * @Version 1.0
 */
@Configuration
public class QuartzStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(QuartzStartupListener.class);

    @Resource
    private Scheduler powerTradeAggregationScheduler;
    @Resource
    private Trigger powerTradeAggregationJobTrigger;
    @Resource
    private JobDetail powerTradeAggregationJobDetail;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (powerTradeAggregationJobDetail == null) {
            throw new RuntimeException("powerTradeAggregationJobDetail not found ....");
        }
        if (powerTradeAggregationJobTrigger == null) {
            throw new RuntimeException("powerTradeAggregationJobTrigger not found....");
        }
        if (powerTradeAggregationScheduler == null) {
            throw new RuntimeException("powerTradeAggregationScheduler not found....");
        }

        try {
            powerTradeAggregationScheduler.scheduleJob(powerTradeAggregationJobDetail,
                    powerTradeAggregationJobTrigger);
        } catch (SchedulerException e) {
            logger.error("Schedule powerTradeAggregationJobDetail failed", e);
        }
    }
}
