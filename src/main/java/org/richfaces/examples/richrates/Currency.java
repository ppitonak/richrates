package org.richfaces.examples.richrates;

import java.io.Serializable;
import java.util.List;

/**
 * This class represent currency object which is used as object which holds data to display in chart.
 * 
 * @author <a href="mailto:mtomasek@redhat.com">Martin Tomasek</a>
 * 
 */
public class Currency implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private List<CurrencyRecord> data;

    public List<CurrencyRecord> getData() {
        return data;
    }

    public void setData(List<CurrencyRecord> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
