package com.example.tgzoom.sunshine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by tgzoom on 9/22/16.
 */
public class ForecastParserUnitTest {
    @Test
    public void parser_string() throws Exception {
        ForecastParser forecastStringParser = new ForecastParser();
        ArrayList<String> forecast = forecastStringParser.parseJson("{\"city\":{\"id\":5375480,\"name\":\"Mountain View\",\"coord\":{\"lon\":-122.083847,\"lat\":37.386051},\"country\":\"US\",\"population\":0},\"cod\":\"200\",\"message\":0.0369,\"cnt\":7,\"list\":[{\"dt\":1474657200,\"temp\":{\"day\":296.01,\"min\":284.23,\"max\":296.01,\"night\":284.23,\"eve\":294.6,\"morn\":296.01},\"pressure\":995.19,\"humidity\":53,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"02d\"}],\"speed\":2.28,\"deg\":312,\"clouds\":8},{\"dt\":1474743600,\"temp\":{\"day\":295.33,\"min\":280.49,\"max\":299.94,\"night\":286.65,\"eve\":299.94,\"morn\":280.49},\"pressure\":996,\"humidity\":66,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":1.22,\"deg\":329,\"clouds\":0},{\"dt\":1474830000,\"temp\":{\"day\":299.18,\"min\":283.61,\"max\":302.39,\"night\":288.85,\"eve\":302.2,\"morn\":283.61},\"pressure\":994.36,\"humidity\":63,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":1.27,\"deg\":21,\"clouds\":0},{\"dt\":1474916400,\"temp\":{\"day\":295.72,\"min\":286.64,\"max\":298.01,\"night\":288.78,\"eve\":298.01,\"morn\":286.64},\"pressure\":1010.19,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":0.95,\"deg\":111,\"clouds\":0},{\"dt\":1475002800,\"temp\":{\"day\":295.26,\"min\":286.27,\"max\":296.75,\"night\":286.99,\"eve\":296.75,\"morn\":286.27},\"pressure\":1009.22,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":0.52,\"deg\":153,\"clouds\":0},{\"dt\":1475089200,\"temp\":{\"day\":292.43,\"min\":284.33,\"max\":294.63,\"night\":286.57,\"eve\":294.63,\"morn\":284.33},\"pressure\":1012.84,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":0.92,\"deg\":273,\"clouds\":1},{\"dt\":1475175600,\"temp\":{\"day\":291.2,\"min\":284.33,\"max\":293.96,\"night\":286.04,\"eve\":293.96,\"morn\":284.33},\"pressure\":1014.48,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.67,\"deg\":295,\"clouds\":24}]}");
        assertEquals(
                new ArrayList<String>(Arrays.asList(
                        "Fri Sep 23 - 296/284 - Clear",
                        "Sat Sep 24 - 300/280 - Clear",
                        "Sun Sep 25 - 302/284 - Clear",
                        "Mon Sep 26 - 298/287 - Clear",
                        "Tue Sep 27 - 297/286 - Clear",
                        "Wed Sep 28 - 295/284 - Rain",
                        "Thu Sep 29 - 294/284 - Rain"
                )) ,
                forecast
        );
    }


}
