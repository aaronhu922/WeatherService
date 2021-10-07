package com.epam.aaronhu.weather.service;

import com.epam.aaronhu.weather.exception.ApiException;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.runner.RunWith;
//import org.mockito.Mockito.*;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WeatherServiceRateLimiterTests {

    //    @MockBean
//    RestTemplate restTemplate;
    @Autowired
    WeatherService weatherService;

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

//    @Test
//    public void testRejectRequestsIfExceeds100TPS() {
//
//
//        ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"10101\":\"北京\",\"10102\":\"上海\",\"10103\":\"天津\",\"10104\":\"重庆\",\"10105\":\"黑龙江\",\"10106\":\"吉林\",\"10107\":\"辽宁\"}", HttpStatus.OK);
//
//        when(restTemplate.exchange(
//                "test",
//                HttpMethod.GET,
//                null,
//                String.class,
//                "code")).thenAnswer(new Answer<String>() {
//
//            @Override
//            public String answer(InvocationOnMock invocation) throws Throwable {
//                Thread.sleep(5000);
//                return "";
//            }
//        });
//
//        ResponseEntity<String> response = restTemplate
//                .exchange("test", HttpMethod.GET, null, String.class, "code");
//        try {
//            Optional<Integer> op3 = weatherService.getTemperature("江苏", "南京", "江宁");
//            Assert.assertNull(op3);
//        } catch (ApiException e) {
//            Assert.assertEquals(1001, e.getCode());
//            log.error("Error code {}, and message {}", e.getCode(), e);
//        }

//        ExecutorService service = Executors.newFixedThreadPool(5); //execute submit
//        for (int i = 0; i < 6; i++) {
//            service.execute(() -> {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(Thread.currentThread().getName());
//            });
//        }
//        log.info("return value {}, message {}", response.getStatusCode(), response.getBody());
//    }

}
