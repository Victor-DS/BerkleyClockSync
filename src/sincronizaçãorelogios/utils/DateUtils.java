/*
 * The MIT License
 *
 * Copyright 2017 Victor Santiago.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sincronizaçãorelogios.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Collection of useful Date-related methods.
 *
 * @author Victor Santiago
 */
public class DateUtils {

    public static final int SECOND = 1000;

    static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        
    /**
     * Transforms a Date into a String.
     * 
     * @param date The date object to be transformed as String.
     * @return String in the format MM/dd/yyyy HH:mm:ss.
     */
    public static String toString(Date date) {
        return df.format(date);
    }
    
    /**
     * Transforms the millis time into a readable String.
     * 
     * @param time millis time value.
     * @return String in the format MM/dd/yyyy HH:mm:ss.
     */
    public static String toString(long time) {
        return toString(new Date(time));
    }
    
    /**
     * Transformar a String into a Date object.
     * 
     * @param date String in the format MM/dd/yyyy HH:mm:ss.
     * @return Date object with the String's date.
     * @throws ParseException Thrown if the string is not properly formatted.
     */
    public static Date toDate(String date) throws ParseException {
        return df.parse(date);
    }
    
    /**
     * Returns the system's current time in Millis.
     * 
     * @return Long millis value.
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }
    
    /**
     * Returns the current time in String format.
     * 
     * @return Time in the format MM/dd/yyyy HH:mm:ss.
     */
    public static String getCurrentTimeString() {
        return toString(new Date(getCurrentTime()));
    }
    
    /**
     * Gets the current date and adds a random value between -60s to +60s.
     * 
     * @return Date with the random time.
     */
    public static Date getRandomTime() {
        Long current = getCurrentTime();
        int randomDiff = ThreadLocalRandom.current()
                .nextInt(-60 * SECOND, 60 * SECOND);

        return new Date(current + randomDiff);
    }
    
    /**
     * Returnds the difference, in millis between b - a.
     * 
     * @param a date A.
     * @param b date B.
     * @return Millis representing the difference.
     */
    public static long getDifference(Date a, Date b) {
        return b.getTime() - a.getTime();
    }
    
}
