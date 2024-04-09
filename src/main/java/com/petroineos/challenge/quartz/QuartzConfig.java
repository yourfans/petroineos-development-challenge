package com.petroineos.challenge.quartz;

import com.petroineos.challenge.quartz.jobs.PowerTradeAggregationJob;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * It defines the trigger and the job.
 * @Author Dean Zhu
 * @Date 2024-04-08
 * @Version 1.0
 */
@Configuration
public class QuartzConfig {
    @Value("${jobStartDelay}")
    private long jobStartDelay;

    @Value("${jobInterval}")
    private long jobInterval;

    @Bean
    public JobDetailFactoryBean powerTradeAggregationJobDetail() {
        JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
        jobDetail.setName("powerTradeAggregationJobDetail");
        jobDetail.setJobClass(PowerTradeAggregationJob.class);
        jobDetail.setDurability(true);
        return jobDetail;
    }

    @Bean
    public SimpleTriggerFactoryBean powerTradeAggregationJobTrigger(JobDetail powerTradeAggregationJobDetail) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(powerTradeAggregationJobDetail);
        trigger.setName("powerTradeAggregationJobTrigger");
        trigger.setStartDelay(jobStartDelay);
        trigger.setRepeatInterval(jobInterval);
        return trigger;
    }
}
