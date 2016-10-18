/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tgzoom.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.tgzoom.sunshine.data.TestUtilities;
import com.example.tgzoom.sunshine.data.WeatherContract;
import com.example.tgzoom.sunshine.data.WeatherDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class TestGregorianCalendar extends AndroidTestCase {
    public void testCreateDb() throws Throwable {
        Calendar calendar = new GregorianCalendar();

        int lastDay = 7;

        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");

        for (int firstDay = 0; firstDay<lastDay;firstDay++){
            calendar.add(Calendar.DAY_OF_MONTH,2);
            assertEquals("Invalid day!" + calendar.getTimeInMillis(),shortenedDateFormat.format(calendar.getTimeInMillis()) == "Wed Oct 17");
        }
    }
}
