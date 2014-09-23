package org.richfaces.examples.richrates;

/**
 * This is concrete point which will be represented in chart.
 * @author <a href="mailto:mtomasek@redhat.com">Martin Tomasek</a>
 *
 */
public class CurrencyRecord {

    //Note there is Number cause String not working right now
    private final Number month;
    private final Number rate;
    
    public CurrencyRecord(Number month, Number rate){
        this.month = month;
        this.rate = rate;
    }

    public Number getMonth() {
        return month;
    }

    public Number getRate() {
        return rate;
    }
    
    
}
