package com.epam.aaronhu.weather.service;

import com.epam.aaronhu.weather.model.*;
import com.epam.aaronhu.weather.support.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WeatherService implements ApplicationRunner {

    private static final String PROVINCE_URL = "http://www.weather.com.cn/data/city3jdata/china.html";
    private static final String CITY_URL = "http://www.weather.com.cn/data/city3jdata/provshi/{code}.html";
    private static final String COUNTRY_URL = "http://www.weather.com.cn/data/city3jdata/station/{code}.html";
    private static final String WEATHER_URL = "http://www.weather.com.cn/data/sk/{code}.html";

    @Autowired
    private RestTemplate restTemplate;

    Map<String, String> cachedProsAndCities = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //TODO: Load all the county code into cache. eg, {"江苏南京江宁":"101190104"}
        //it can reject the attack with false name.
        //initCache();
//        queryProvince();
//        String jiangsu = getProvinceCode("江苏");
//        log.info("jiangsu code is {}.", jiangsu);
//        log.info("null code is {}.", null==jiangsu);
//        String suzhou = getCityCode(jiangsu, "南京");
//        log.info("city: {}", suzhou);
//        String county = getCountyCode(jiangsu + suzhou, "江宁");
//        log.info("county: {}", jiangsu + suzhou + county);
//        Optional<Integer> op = getTemperature(jiangsu + suzhou + county);
//        log.info("final temp is {}", op.get());
//        String lishui = getCountyCode(jiangsu + suzhou, "溧水");
//        Optional<Integer> op2 = getTemperature(jiangsu + suzhou + lishui);
//        log.info("lishui temp is {}", op2.get());
        Optional<Integer> op3;
        try {
            op3 = getTemperature("江苏", "南京", "江宁");
            log.info("江宁 temp is {}", op3.isPresent() ? op3.get() : null);
            op3 = getTemperature("江苏", "南京1", "溧水");
            log.info("溧水 temp is {}", op3.isPresent() ? op3.get() : null);

        } catch (ApiException e) {
            log.error("Error code {}, and message {}", e.getCode(), e.getMessage());
//            e.printStackTrace();
        }
        try {
            op3 = getTemperature("江苏", "南京", "江宁11");
            log.info("江宁11 temp is {}", op3.isPresent() ? op3.get() : null);
        } catch (ApiException e) {
            log.error("Error code {}, and message {}", e.getCode(), e.getMessage());
//            e.printStackTrace();
        }

        try {
            op3 = getTemperature("江苏1", "南京", "江宁");
            log.info("江苏1 temp is {}", op3.isPresent() ? op3.get() : null);
        } catch (ApiException e) {
            log.error("Error code {}, and message {}", e.getCode(), e.getMessage());
//            e.printStackTrace();
        }
    }

//    private void initCache() {
//    }

    public Optional<Integer> getTemperature(String province, String city, String county) throws ApiException {
        String provinceCode = getProvinceCode(province);
        log.info("provinceCode: {}", provinceCode);
        if (null == provinceCode || provinceCode.isEmpty()) throw new ApiException(1001, "Invalid province name!");

        String cityCode = getCityCode(provinceCode, city);
        log.info("cityCode: {}", cityCode);
        if (null == cityCode || cityCode.isEmpty()) throw new ApiException(1002, "Invalid city name!");

        String countyCode = getCountyCode(provinceCode + cityCode, county);
        log.info("countyCode: {}", countyCode);
        if (null == countyCode || countyCode.isEmpty()) throw new ApiException(1003, "Invalid county name!");
        String code = provinceCode + cityCode + countyCode;
        log.info("code: {}", code);
        return getTemperature(code);
    }

    public String getProvinceCode(String province) {
        if (!cachedProsAndCities.containsKey(province)) {
            log.info("Need to query remote server to get provinces!");
            queryProvince();
        }
        return cachedProsAndCities.get(province);
    }

    public String getCityCode(String proCode, String city) {
        String key = proCode + city;
        if (!cachedProsAndCities.containsKey(key))
            queryCity(proCode);
        return cachedProsAndCities.get(key);
    }

    public String getCountyCode(String preCode, String county) {
        String key = preCode + county;
        if (!cachedProsAndCities.containsKey(key))
            queryCounty(preCode);
        return cachedProsAndCities.get(key);
    }

    public Optional<Integer> getTemperature(String countyCode) throws ApiException {

        ResponseEntity<String> response = restTemplate
                .exchange(WEATHER_URL, HttpMethod.GET, null, String.class, countyCode);
        String body = response.getBody();
        log.info("response status: {}, content: {}", response.getStatusCode(), body);
        try {
            Map<String, Weatherinfo> map = new ObjectMapper().readValue(body, new TypeReference<HashMap<String, Weatherinfo>>() {
            });
            String temp = map.get("weatherinfo").getTemp();
            log.info("Weather: {}", temp);
            return Optional.ofNullable(Math.round(Float.parseFloat(temp)));
        } catch (JsonProcessingException | NumberFormatException e) {
            log.error("Unexpected return value when to get temp with code {}, error message is: {}", countyCode, e.getMessage());
            throw new ApiException(1004, "Failed to get temperature with county code " + countyCode);
        }
    }

    private void queryProvince() {
        queryCodeByName("", PROVINCE_URL);
    }

    private void queryCity(String code) {
        queryCodeByName(code, CITY_URL);
    }

    private void queryCounty(String code) {
        queryCodeByName(code, COUNTRY_URL);
    }

    private void queryCodeByName(String code, String url) {
        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.GET, null, String.class, code);

        String body = response.getBody();
        log.info("response status: {}, content: {}", response.getStatusCode(), body);
        try {
            Map<String, String> map = new ObjectMapper().readValue(body, new TypeReference<HashMap<String, String>>() {
            });
            for (Map.Entry<String, String> entry : map.entrySet()) {
                cachedProsAndCities.put(code + entry.getValue(), entry.getKey());
            }
            log.info("response: {}", cachedProsAndCities);
        } catch (JsonProcessingException e) {
            log.error("Unexpected return value when to get {} with param {}", url, code);
        }
    }
}
