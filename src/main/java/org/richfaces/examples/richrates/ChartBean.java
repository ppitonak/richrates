/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2010-2013, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.richfaces.examples.richrates;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Past;

import org.joda.time.DateTime;
import org.richfaces.event.DropEvent;
import org.richfaces.examples.richrates.annotation.ExchangeRates;
import org.richfaces.examples.richrates.annotation.IssueDate;
/**
 * Bean used on the page with chart. Is is possible to draw a chart for one currency for selected time range.
 * 
 * @author <a href="mailto:ppitonak@redhat.com">Pavol Pitonak</a>
 * @since 4.1
 */
@Named
@SessionScoped
public class ChartBean implements Serializable {

    private static final long serialVersionUID = -1L;
    @Inject
    @IssueDate
    @Past(message = "To date: Future date cannot be selected.")
    private Date toDate;
    @Past(message = "From date: Future date cannot be selected.")
    private Date fromDate;
    @Inject
    @ExchangeRates
    private Map<Date, Map<String, Double>> currencies;
    
    private Currency currency;
    
    private String selectedCurrency;
    private Logger logger;

    /**
     * Initializes class' fields.
     */
    @PostConstruct
    private void initialize() {
        logger = Logger.getLogger(ChartBean.class.toString());
        selectedCurrency = "USD";
        fromDate = new DateTime(toDate).minusDays(30).toDate();
        parseCurrencyData();
    }

    /**
     * Getter for start date.
     * 
     * @return start date for chart
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * Setter for start date.
     * 
     * @param fromDate
     *            start date for chart
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Getter for end date.
     * 
     * @return end date for chart
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Setter for end date.
     * 
     * @param toDate
     *            end date for chart
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * Getter for selected currency.
     * 
     * @return ISO code of the selected currency
     */
    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    /**
     * Creates a data structure needed by chart component. It creates a set of points where X is date and Y is an
     * exchange rate.
     * 
     * @return set of points for chart
     */
    public void parseCurrencyData() {
        DateTime toDate = new DateTime(this.toDate);
        DateTime actualDate = new DateTime(fromDate);
        currency = new Currency();
        currency.setName(getSelectedCurrency());
        List<CurrencyRecord> records = new LinkedList<CurrencyRecord>();
        while (actualDate.compareTo(toDate) <= 0) {
            if (currencies.get(actualDate.toDate()) == null) {
                actualDate = actualDate.plusDays(1);
                continue;
            }
            String dayAndMonth = actualDate.toString("dd.MM");
            Number rate = currencies.get(actualDate.toDate()).get(selectedCurrency).doubleValue();
            records.add(new CurrencyRecord(dayAndMonth, rate));
            actualDate = actualDate.plusDays(1);
        }
        currency.setData(records);
    }

    @AssertTrue(message = "Dates are in wrong order.")
    public boolean isDatesCorrect() {
        logger.log(Level.FINE, "Validating the order of dates");
        return new DateTime(fromDate).isBefore(toDate.getTime());
    }
    
    /**
     * A method used to process drop event.
     * 
     * @param event
     *            a drop event on the chart
     */
    public void processDrop(DropEvent event) {
         selectedCurrency = (String) event.getDragValue();
         parseCurrencyData();
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
