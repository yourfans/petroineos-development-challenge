package com.petroineos.challenge.quartz.jobs;

import com.opencsv.CSVWriter;
import com.petroineos.challenge.adapters.PowerTradeAggregationAdapter;
import com.petroineos.challenge.model.LocalTimeVolume;
import com.petroineos.challenge.model.PowerTrade;
import com.petroineos.challenge.service.PowerService;
import com.petroineos.challenge.utils.JsonUtil;
import com.petroineos.challenge.utils.PetroPeriodUtil;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Dean Zhu
 * @Date 2024-04-08
 * @Version 1.0
 */

public class PowerTradeAggregationJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(PowerTradeAggregationJob.class);

    @Resource
    private PowerTradeAggregationAdapter powerTradeAggregationAdapter;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("PowerTradeAggregationJob begin......");

        powerTradeAggregationAdapter.queryAndAggregatePowerTrade(LocalDateTime.now());

        logger.info("PowerTradeAggregationJob submitted to thread pool.");
    }

}
