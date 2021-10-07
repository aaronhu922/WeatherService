Please implement a method that can fetch and return the temperature of one certain county
in China. Here are the specific features:
- Feature 1: The method signature is `public Optional<Integer> getTemperature(String
province, String city, String county)`.
- Feature 2: If the location is invalid, return reasonable value.
- Feature 3: Add reasonable retry mechanism, cause there might be connection
exception when calling the weather API.
- Feature 4: The method need block invocation if the TPS(transactions per second) is
more than 100.

Here is the APIs flow that shows how to get the temperature of ‘苏州’：
- Get the province code of China
 http://www.weather.com.cn/data/city3jdata/china.html
- Get the city code of one certain province
 http://www.weather.com.cn/data/city3jdata/provshi/10119.html
 ‘10119’ is ‘province code’
- Get the county code of one certain city
 http://www.weather.com.cn/data/city3jdata/station/1011904.html
 ‘1011904’ is ‘province code + city code’
- Get the weather of one certain county
 http://www.weather.com.cn/data/sk/101190401.html
 ‘101190401’ is ‘province code + city code + county code.’

Accepted Criteria:
- JDK 1.8+ Project (Stream API is required)
- Build with Maven.
- Cover all above function requirements.
- Unit Test 