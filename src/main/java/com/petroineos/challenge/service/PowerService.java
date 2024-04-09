package com.petroineos.challenge.service;

import com.petroineos.challenge.model.PowerTrade;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @Author Dean Zhu
 * @Date 2024-04-08
 * @Version 1.0
 */
public interface PowerService {
//    A single interface is provided to retrieve power trades for a specified date. Two methods are provided,
//    one is a synchronous implementation (IEnumerable<PowerTrade> GetTrades(DateTime date);) and the
//    other is asynchronous (Task<IEnumerable<PowerTrade>> GetTradesAsync(DateTime date);). The
//    implementation can use either of these methods. The class PowerService is the actual implementation
//            of this service. The date argument should be the date to retrieve the power position (volume) for.

    /**
     * Retrieve power trades for a specified date.
     * This interface is synchronous implementation.
     * @param date
     * @return
     */
    List<PowerTrade> queryTrades(LocalDateTime date);

    /**
     * Retrieve power trades for a specified date.
     * This interface is asynchronous implementation.
     * @param date
     * @return
     */
    Future<List<PowerTrade>> queryTradesAsync(LocalDateTime date);
}
