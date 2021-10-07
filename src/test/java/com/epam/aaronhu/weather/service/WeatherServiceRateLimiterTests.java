package com.epam.aaronhu.weather.service;

import com.epam.aaronhu.weather.exception.ApiException;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WeatherServiceRateLimiterTests {


    @Autowired
    WeatherService weatherService;

    @After
    public void recoverRateLimiter(){
        weatherService.setRateLimiter(RateLimiter.create(100));
    }

    @Test
    public void testRejectRequestIfExceeds100TPS() throws ApiException {
        weatherService.setRateLimiter(RateLimiter.create(1));

        ExecutorService service = Executors.newFixedThreadPool(2); //execute submit
        service.execute(() -> {
            try {
                Optional<Integer> op3 = weatherService.getTemperature("江苏", "苏州", "苏州");
                Assert.assertTrue(op3.isPresent());
            } catch (ApiException e) {
                Assert.assertEquals(1001, e.getCode());
                log.error("Error code {}, and message {}", e.getCode(), e);
            }
        });

        service.execute(() -> {
            try {
                Optional<Integer> op3 = weatherService.getTemperature("江苏", "苏州", "苏州");
                Assert.assertNull(op3);
            } catch (ApiException e) {
                Assert.assertEquals(1005, e.getCode());
                Assert.assertEquals("Requests number exceeds 1.0 per second, reject the request.", e.getMessage());
                log.error("Error code {}, and message {}", e.getCode(), e);
            }
        });
    }
}
