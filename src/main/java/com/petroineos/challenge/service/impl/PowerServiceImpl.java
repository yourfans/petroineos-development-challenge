package com.petroineos.challenge.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.petroineos.challenge.model.PowerTrade;
import com.petroineos.challenge.service.PowerService;
import com.petroineos.challenge.utils.DateUtil;
import com.petroineos.challenge.utils.JsonUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Two interfaces from the "PowerService.dll" have been mocked, and the related data is stored in the "resources/mock/trades_data.json" file.
 * When the service is started, the data from the JSON file will be loaded into memory. Currently.
 * Data for the period from April 8th to April 30th is prepared.
 * @Author Dean Zhu
 * @Date 2024-04-08
 * @Version 1.0
 */
@Service("powerService")
public class PowerServiceImpl implements PowerService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    //Power trade data mocked from trades_data.json.
    //It was grouped by date.
    private Map<String, List<PowerTrade>> powerTradeMap;

    //Thread pool to mock the asynchronous interface
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Value("${timeZone}")
    private String timeZone;

    @PostConstruct
    private void init() {
        String dataFile = "mock/trades_data.json";
        try {
            //load the data from file.
            Resource resource = new ClassPathResource(dataFile);
            String tradesDataStr = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8")).lines().collect(Collectors.joining("\n"));
            List<PowerTrade> powerTradeList = JsonUtil.string2ListFieldLowerCamelCase(tradesDataStr, new TypeReference<List<PowerTrade>>(){});
            powerTradeMap = powerTradeList.stream().collect(Collectors.groupingBy(x->x.getDate()));
            logger.info("Load power trade succeed: {}", tradesDataStr);
        } catch (Exception e) {
            logger.error("load trades data failed, data file location: {}", dataFile, e);
            powerTradeMap = new LinkedHashMap<>();
        }
    }

    @Override
    public List<PowerTrade> queryTrades(LocalDateTime date) {
        String dateTimeKey = DateUtil.formatDateTime(LocalDateTime.now(), timeZone, DateUtil.PATTERN_POWER_TRADE_DATA);
        if (!powerTradeMap.containsKey(dateTimeKey)) {
            return Collections.emptyList();
        }
        return powerTradeMap.get(dateTimeKey);
    }

    @Override
    public Future<List<PowerTrade>> queryTradesAsync(LocalDateTime date) {
        return executor.submit(() -> {
            //Sleep 10s to simulate a poorly performing interface
            Thread.sleep(10000L);
            String dateTimeKey = DateUtil.formatDateTime(LocalDateTime.now(), timeZone, DateUtil.PATTERN_POWER_TRADE_DATA);
            if (!powerTradeMap.containsKey(dateTimeKey)) {
                return Collections.emptyList();
            }
            return powerTradeMap.get(dateTimeKey);
        });
    }

}
