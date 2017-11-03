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
package sincronizaçãorelogios.model;

import java.util.Date;
import java.util.List;
import sincronizaçãorelogios.utils.DateUtils;

/**
 * Handles all Berkley-related algorithm calculations.
 *
 * @author Victor Santiago
 */
public class Berkley {
    
    private static long TOLERANCE = 45 * DateUtils.SECOND;
    
    public Date getNewDate(List<Date> allSlaves, Date slave, Date master) {
        final int average = getAverage(allSlaves, master);

        long diff = getDifference(slave, master);
        long time = slave.getTime();
        
        slave.setTime(time + average - diff);
        
        return slave;
    }

    public List<Date> getNewDates(List<Date> slaves, Date master) {
        final int average = getAverage(slaves, master);
        
        long diff, time;
        for (Date slave : slaves) {
            diff = getDifference(slave, master);
            time = slave.getTime();
            
            slave.setTime(time + average - diff);
        }
        
        return slaves;
    }
    
    private int getAverage(List<Date> slaves, Date master) {
        int total = 0, quantity = slaves.size();
        
        for (Date slave : slaves) {
            total += getDifference(slave, master);
        }
        
        return total / quantity;
    }
    
    private long getDifference(Date slave, Date master) {
        if (isFailedRequest(slave, master)) {
            return 0;
        }
        
        return DateUtils.getDifference(slave, master);
    }
    
    private boolean isFailedRequest(Date slave, Date master) {
        return DateUtils.getDifference(slave, master) > TOLERANCE;
    }
    
}
