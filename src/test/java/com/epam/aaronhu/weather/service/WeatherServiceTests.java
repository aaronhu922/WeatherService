package com.epam.aaronhu.weather.service;

import com.epam.aaronhu.weather.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WeatherServiceTests {

    private static final String PROVINCE_URL = "http://www.weather.com.cn/data/city3jdata/china.html";
    private static final String CITY_URL = "http://www.weather.com.cn/data/city3jdata/provshi/{code}.html";
    private static final String COUNTRY_URL = "http://www.weather.com.cn/data/city3jdata/station/{code}.html";
    private static final String WEATHER_URL = "http://www.weather.com.cn/data/sk/{code}.html";

    @Autowired
    WeatherService weatherService;

    @Test
    public void testGetTemperature() {
        Optional<Integer> op3;
        try {
            op3 = weatherService.getTemperature("江苏", "南京", "江宁");
            Assert.assertTrue(op3.isPresent());
        } catch (ApiException e) {
            log.error("Error code {}, and message {}", e.getCode(), e);
        }
    }

    @Test(expected = ApiException.class)
    public void testGetTemperatureInvalidProvince() throws ApiException {
        Optional<Integer> op3 = weatherService.getTemperature("江苏1", "南京", "溧水");
        Assert.assertNull(op3);
    }

    @Test
    public void testGetTemperatureInvalidPro() {
        try {
            Optional<Integer> op3 = weatherService.getTemperature("江苏1", "南京", "江宁");
            Assert.assertNull(op3);
        } catch (ApiException e) {
            Assert.assertEquals(1001, e.getCode());
            log.error("Error code {}, and message {}", e.getCode(), e);
        }
    }

    @Test
    public void testGetTemperatureInvalidCity() {
        try {
            Optional<Integer> op3 = weatherService.getTemperature("江苏", "南京1", "江宁");
            Assert.assertNull(op3);
        } catch (ApiException e) {
            Assert.assertEquals(1002, e.getCode());
            log.error("Error code {}, and message {}", e.getCode(), e);
        }
    }

    @Test
    public void testGetTemperatureInvalidCounty() {
        try {
            Optional<Integer> op3 = weatherService.getTemperature("江苏", "南京", "江宁1");
            Assert.assertNull(op3);
        } catch (ApiException e) {
            Assert.assertEquals(1003, e.getCode());
            log.error("Error code {}, and message {}", e.getCode(), e);
        }
    }

    @Test
    public void testGetTemperatureInvalidCode() {
        try {
            Optional<Integer> op3 = weatherService.getTemperature("1111111");
            Assert.assertNull(op3);
        } catch (ApiException e) {
            Assert.assertEquals(1004, e.getCode());
            log.error("Error code {}, and message {}", e.getCode(), e);
        }
    }

    @Test
    public void testGetProvince() {
        String code = weatherService.getProvinceCode("江苏");
        Assert.assertEquals("10119", code);
        Assert.assertTrue(weatherService.cachedProsAndCities.containsKey("江苏"));
        Assert.assertFalse(weatherService.cachedProsAndCities.containsKey("江苏1"));
    }

    @Test
    public void testGetCityCode() {
        String code = weatherService.getCityCode("10119", "苏州");
        Assert.assertEquals("04", code);
        Assert.assertTrue(weatherService.cachedProsAndCities.containsKey("10119苏州"));
        Assert.assertFalse(weatherService.cachedProsAndCities.containsKey("10119不存在"));
    }

    @Test
    public void testGetCountyCode() {
        String code = weatherService.getCountyCode("1011904", "苏州");
        Assert.assertEquals("01", code);
        Assert.assertTrue(weatherService.cachedProsAndCities.containsKey("1011904苏州"));
        Assert.assertFalse(weatherService.cachedProsAndCities.containsKey("1011904不存在"));
    }

    @Test
    public void testGetProvinceInvalid() {
        String code = weatherService.getProvinceCode("江苏1");
        Assert.assertNull(code);
    }

    @Test
    public void testGetCityCodeInvalid() {
        String code = weatherService.getCityCode("10119", "不存在");
        Assert.assertNull(code);
    }

    @Test
    public void testGetCountyCodeInvalid() {
        String code = weatherService.getCountyCode("1011904", "不存在");
        Assert.assertNull(code);
    }

    @Test
    public void testQueryProvince() {
        weatherService.queryCodeByName("", PROVINCE_URL);
        Assert.assertTrue(weatherService.cachedProsAndCities.containsKey("山东"));
    }

    @Test
    public void testQueryCity() {
        weatherService.queryCodeByName("10119", CITY_URL);
        Assert.assertTrue(weatherService.cachedProsAndCities.containsKey("10119苏州"));
    }

    @Test
    public void testQueryCounty() {
        weatherService.queryCodeByName("1011904", COUNTRY_URL);
        Assert.assertTrue(weatherService.cachedProsAndCities.containsKey("1011904苏州"));
    }

    @Test(expected = ResourceAccessException.class)
    public void testRetryHandler() {
        weatherService.queryCodeByName("1011904", "http://localhost:33333/noexists");
    }
}
