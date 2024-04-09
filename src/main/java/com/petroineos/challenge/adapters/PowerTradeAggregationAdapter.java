package com.petroineos.challenge.adapters;

import com.opencsv.CSVWriter;
import com.petroineos.challenge.model.LocalTimeVolume;
import com.petroineos.challenge.model.PowerTrade;
import com.petroineos.challenge.service.PowerService;
import com.petroineos.challenge.utils.DateUtil;
import com.petroineos.challenge.utils.JsonUtil;
import com.petroineos.challenge.utils.PetroPeriodUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * A cached thread pool defined, named "executorService".
 * When a task is triggered, it is submitted to the "executorService" for execution.
 * The "executorService" retrieves an available thread from the thread pool to execute the task.
 * If there are no available threads at the moment, a new thread will be created.
 * The threads have a maximum lifetime of 60 seconds.
 * If a thread is not used for more than 60 seconds, it will be removed from the thread pool.
 * @Author Dean Zhu
 * @Date 2024-04-09
 * @Version 1.0
 */
@Component
public class PowerTradeAggregationAdapter {
    private static final Logger logger = LoggerFactory.getLogger(PowerTradeAggregationAdapter.class);
    @Resource
    private PowerService powerService;
    @Value("${csvFileLocation}")
    private String csvFileLocation;
    @Value("${timeZone}")
    private String timeZone;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public Future<Boolean> queryAndAggregatePowerTrade(LocalDateTime date) {
        Future<Boolean> future = executorService.submit(() -> {
            long now = System.currentTimeMillis();
            String threadIdentity = String.format("%s-%s", Thread.currentThread().getName(), Thread.currentThread().getId());
            try {
                logger.info("Query and aggregate power trade begin, thread: {}", threadIdentity);

                List<PowerTrade> powerTradeList = powerService.queryTrades(date);
                write2csv(mergePowerTradeList(powerTradeList), date);
                long timeCost = System.currentTimeMillis() - now;

                logger.info("Query and aggregate power trade, time cost: {}ms, thread: {}", timeCost, threadIdentity);
                return true;
            } catch (Exception e) {
                //TODO notify someone
                logger.error("Query and aggregate power trade failed, time cost: {}, thread: {}",
                        System.currentTimeMillis() - now, threadIdentity, e);
            }

            return false;
        });
        return future;
    }

    private List<LocalTimeVolume> mergePowerTradeList(List<PowerTrade> powerTradeList) {
        if (CollectionUtils.isEmpty(powerTradeList)) {
            logger.error("Power Trade data corrupted, empty powerTradeList found.");
            return Collections.emptyList();
        }

        if (powerTradeList.size() != 2) {
            logger.error("Power Trade data corrupted, two trade positions needed, corrupted data: {}",
                    JsonUtil.obj2String(powerTradeList));
            return Collections.emptyList();
        }

        PowerTrade powerTrade1st = powerTradeList.get(0);
        PowerTrade powerTrade2nd = powerTradeList.get(1);
        Map<Integer, Integer> powerTrade1stPeriodVolumeMap = powerTrade1st.getTrades().stream()
                .collect(Collectors.toMap(x -> x.getPeriod(), x -> x.getVolume()));
        Map<Integer, Integer> powerTrade2ndPeriodVolumeMap = powerTrade2nd.getTrades().stream()
                .collect(Collectors.toMap(x -> x.getPeriod(), x -> x.getVolume()));

        List<LocalTimeVolume> localTimeVolumeList = new ArrayList<>();
        for (int i = PetroPeriodUtil.MIN_PERIOD; i <= PetroPeriodUtil.MAX_PERIOD; i++) {
            int volumeAggregation = powerTrade1stPeriodVolumeMap.getOrDefault(i, 0)
                    + powerTrade2ndPeriodVolumeMap.getOrDefault(i, 0);
            String localTime = PetroPeriodUtil.getTimeByPeriod(i);
            localTimeVolumeList.add(new LocalTimeVolume(localTime, volumeAggregation));
        }

        return localTimeVolumeList;
    }

    private void write2csv(List<LocalTimeVolume> localTimeVolumeList, LocalDateTime dateTime) {
        CSVWriter writer = null;
        try {
            String dateTimeAppendix = DateUtil.formatDateTime(dateTime, timeZone, DateUtil.PATTERN_AGGREGATION_CSV_FILE);
            String fileName = String.format("PowerPosition_%s.csv", dateTimeAppendix);
            File folder = new File(csvFileLocation);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String filePath = csvFileLocation + "/" + fileName;
            writer = new CSVWriter(new FileWriter(filePath));
            writer.writeAll(toCSVRecordList(localTimeVolumeList));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private List<String[]> toCSVRecordList(List<LocalTimeVolume> localTimeVolumeList) {
        List<String[]> res = new ArrayList<>();

        String[] header = new String[]{"LocalTime", "Volume"};
        res.add(header);

        for (LocalTimeVolume localTimeVolume : localTimeVolumeList) {
            String[] csvRecord = new String[]{localTimeVolume.getLocalTime(), String.valueOf(localTimeVolume.getVolume())};
            res.add(csvRecord);
        }

        return res;
    }
}
