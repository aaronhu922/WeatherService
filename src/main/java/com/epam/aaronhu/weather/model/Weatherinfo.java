package com.epam.aaronhu.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weatherinfo{
    public String city;
    public String cityid;
    public String temp;
    @JsonProperty("WD")
    public String wD;
    @JsonProperty("WS")
    public String wS;
    @JsonProperty("SD")
    public String sD;
    @JsonProperty("AP")
    public String aP;
    public String njd;
    @JsonProperty("WSE")
    public String wSE;
    public String time;
    public String sm;
    public String isRadar;
    @JsonProperty("Radar")
    public String radar;
}
