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

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.richfaces.examples.richrates.annotation.CurrenciesNames;
import org.richfaces.examples.richrates.annotation.ExchangeRates;
import org.richfaces.examples.richrates.annotation.IssueDate;

/**
 * The main bean of the application. It downloads a data file from the Internet and parses it. It contains also long
 * names for currencies (e.g. American Dollar for USD).
 * 
 * @author <a href="mailto:ppitonak@redhat.com">Pavol Pitonak</a>
 * @since 4.1
 */
@Named
@ApplicationScoped
public class RatesBean implements Serializable {

    private static final long serialVersionUID = -1L;
    private Logger logger;
    private Map<String, String> currencyNames;
    private Map<Date, Map<String, Double>> currencies;
    private TimeZone timeZone;
    // the date when the newest list has been issued
    private Date issueDate;

    /**
     * Initializes the private fields, downloads the data from the Internet and parses the downloaded XML.
     */
    @PostConstruct
    private void initialize() {
        logger = Logger.getLogger(RatesBean.class.toString());
        timeZone = TimeZone.getTimeZone("CET"); // ECB is in CET time zone
        logger.info("Using " + timeZone.getDisplayName());
        currencyNames = new HashMap<String, String>();

        currencyNames.put("AUD", "Australian Dollar");
        currencyNames.put("BGN", "Bulgarian Lev");
        currencyNames.put("BRL", "Brazilian Real");
        currencyNames.put("CAD", "Canadian Dollar");
        currencyNames.put("CHF", "Swiss Franc");
        currencyNames.put("CNY", "Chinese Yuan Renminbi");
        currencyNames.put("CZK", "Czech Koruna");
        currencyNames.put("DKK", "Danish Krone");
        currencyNames.put("GBP", "British Pound");
        currencyNames.put("HRK", "Croatian Kuna");
        currencyNames.put("HUF", "Hungarian Forint");
        currencyNames.put("IDR", "Indonesian Rupiah");
        currencyNames.put("INR", "Indian Rupee");
        currencyNames.put("JPY", "Japan Yen");
        currencyNames.put("KRW", "South Korean Won");
        currencyNames.put("MXN", "Mexican Peso");
        currencyNames.put("MYR", "Malaysian Ringgit");
        currencyNames.put("NOK", "Norwegian Kroner");
        currencyNames.put("NZD", "New Zealand Dollar");
        currencyNames.put("PHP", "Philippine Peso");
        currencyNames.put("PLN", "Polish Zloty");
        currencyNames.put("RON", "Romanian Lei");
        currencyNames.put("RUB", "Russian Rouble");
        currencyNames.put("SEK", "Swedish Krona");
        currencyNames.put("SGD", "Singapore Dollar");
        currencyNames.put("THB", "Thai Baht");
        currencyNames.put("TRY", "Turkish Lira");
        currencyNames.put("USD", "American Dollar");
        currencyNames.put("ZAR", "South African Rand");

        currencies = new HashMap<Date, Map<String, Double>>();
        getRates();
    }

    /**
     * Downloads an XML file from European Central Bank containing exchange rates for over 30 currencies for las 90 days
     * and parses it.
     */
    private void getRates() {
        logger.info("Parsing exchange rates");

        try {
            // TODO put into external file
            // http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml
            // http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml
            // http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
            URL url = new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml");
            Document document = new SAXBuilder().build(url);

            // TODO put into external file
            Namespace namespace = Namespace.getNamespace("http://www.ecb.int/vocabulary/2002-08-01/eurofxref");

            // TODO use XPath instead of the following line
            List<Element> dateElements = (List<Element>) document.getRootElement().getChild("Cube", namespace)
                .getChildren("Cube", namespace);

            String timeAttribute = null;
            Date date = null;

            // get newest date
            timeAttribute = dateElements.iterator().next().getAttributeValue("time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            issueDate = sdf.parse(timeAttribute);

            for (Element e : dateElements) {
                timeAttribute = e.getAttributeValue("time");
                date = sdf.parse(timeAttribute);

                List<Element> children = (List<Element>) e.getChildren("Cube", namespace);
                Map<String, Double> oneDayRates = new HashMap<String, Double>();
                for (Element node : children) {
                    String currency = node.getAttributeValue("currency");
                    double rate = node.getAttribute("rate").getDoubleValue();
                    oneDayRates.put(currency, rate);
                }

                currencies.put(date, oneDayRates);
            }

            logger.info("List with exchange rates parsed");

        } catch (JDOMException e) {
            logger.log(Level.SEVERE, "Could not parse XML with exchange rates");
            e.printStackTrace();
        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Could not parse date format");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Could not parse the URL with exchange rates");
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An I/O error prevents a document from being fully parsed");
            e.printStackTrace();
        }
    }

    /**
     * Getter for the time zone;
     * 
     * @return time zone that will be used in application.
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Getter for the date when the exchange rates were issued. Usually actual day or previous working day.
     * 
     * @return issue date
     */
    @Produces
    @Named
    @IssueDate
    public Date getIssueDate() {
        return issueDate;
    }

    /**
     * Getter for exchange rates.
     * 
     * @return map containing a mapping of dates, ISO codes of currencies and exchange rates
     */
    @Produces
    @Named
    @ExchangeRates
    public Map<Date, Map<String, Double>> getExchangeRates() {
        return currencies;
    }

    /**
     * Getter for currency names.
     * 
     * @return map containing a mapping of ISO code of currency and its name, e.g. EUR -> Euro.
     */
    @Produces
    @Named
    @CurrenciesNames
    public Map<String, String> getCurrenciesNames() {
        return currencyNames;
    }

    /**
     * Getter for ISO codes of all available currencies.
     * 
     * @return list of ISO codes of all available currencies.
     */
    public List<String> getCodes() {
        return new ArrayList<String>(currencyNames.keySet());
    }

}
