# WeatherService
Weather service example for Epam.

## Requirement
[Weather service api definition.](requiremet.md)


### Prerequisites
* Springboot
* JDK 1.8+
* Maven


###  API
```
public Optional<Integer> getTemperature(String province, String city, String county) {
}
```
### Error Code
|code| message|
| --- | --------- |
|1001|	Invalid province name!|
|1002|	Invalid city name!|
|1003|	Invalid county name!|
|1004|	Failed to get temperature with {county code}!|
|1005|	Requests number exceeds rate limit!|

### Build

Import project to your IDEA or Eclipse. 
or build with Maven.
```
 mvn install
 ```


## Running the tests
* Run the tests in  IDEA or Eclipse.
Or by mvn commands. 
```
 mvn test
 ```

## TODO:
* Init all the county code into cache. It can get the code from cache, no need to query from remote every time.
It can avoid the attacks with invalid names. 
```
{
"江苏南京江宁":"101190104"
"江苏苏州苏州":"101190401"
......
}
```


## Versioning
* 0.0.1-SANPSHOT, the initial version of this framework. 

## Authors

* **Aaron Hu** - *Initial work* 

## License


## Acknowledgments


