package com.example.tgzoom.sunshine;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by tgzoom on 9/22/16.
 */
public class ForecastParserUnitTest {
    @Test
    public void parser_string() throws Exception {
        ForecastParser forecastStringParser = new ForecastParser();
        String[] parsed = forecastStringParser.parseJson("{\"message\":\"accurate\",\"cod\":\"200\",\"count\":1,\"list\":[{\"id\":0,\"name\":\"Mountain View\",\"coord\":{\"lat\":37.4056,\"lon\":-122.0775},\"main\":{\"temp\":21.28,\"humidity\":49,\"pressure\":1016.3,\"temp_min\":17.22,\"temp_max\":22.78},\"dt\":1474584828,\"wind\":{\"speed\":2.92,\"deg\":302.504},\"sys\":{\"country\":\"US\"},\"clouds\":{\"all\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"Sky is Clear\",\"icon\":\"01d\"}]}]}");
        assertEquals(Arrays.asList("accurate","200",1),parsed);
    }


}
