package org.richfaces.examples.richrates;

/**
 * This is concrete point which will be represented in chart.
 * @author <a href="mailto:mtomasek@redhat.com">Martin Tomasek</a>
 *
 */
public class CurrencyRecord {

    //Note there is Number cause String not working right now
    private final String month;
    private final Number rate;
    
    public CurrencyRecord(String month, Number rate){
        this.month = month;
        this.rate = rate;
    }

    public String getMonth() {
        return month;
    }

    public Number getRate() {
        return rate;
    }
    
    
}
