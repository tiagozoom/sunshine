package com.example.tgzoom.sunshine;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Pair;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ForecastParserUnitTest {

    private ForecastParser forecastParser;

    @Before
    public void createLogHistory() {
        forecastParser = new ForecastParser();
    }

    @Test
    public void test() throws JSONException {
        // Set up the Parcelable object to send and receive.
        forecastParser.parseJson("");

        // Verify that the received data is correct.
        assertNotNull(forecastParser);
    }
}
